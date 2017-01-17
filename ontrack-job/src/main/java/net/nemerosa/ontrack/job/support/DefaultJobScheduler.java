package net.nemerosa.ontrack.job.support;

import net.nemerosa.ontrack.common.Time;
import net.nemerosa.ontrack.job.*;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class DefaultJobScheduler implements JobScheduler {

    private final Logger logger = LoggerFactory.getLogger(JobScheduler.class);

    private final JobDecorator jobDecorator;
    private final ScheduledExecutorService schedulerPool;
    private final JobListener jobListener;
    private final BiFunction<ExecutorService, Job, ExecutorService> jobPoolProvider;

    private final Map<JobKey, JobScheduledService> services = new ConcurrentHashMap<>(new TreeMap<>());
    private final AtomicBoolean schedulerPaused;
    private final boolean scattering;
    private final double scatteringRatio;

    private final AtomicLong idGenerator = new AtomicLong();

    public DefaultJobScheduler(
            JobDecorator jobDecorator,
            ScheduledExecutorService schedulerPool,
            JobListener jobListener,
            boolean initiallyPaused,
            boolean scattering,
            double scatteringRatio
    ) {
        this(
                jobDecorator,
                schedulerPool,
                jobListener,
                initiallyPaused,
                (executorService, job) -> executorService,
                scattering,
                scatteringRatio
        );
    }

    public DefaultJobScheduler(
            JobDecorator jobDecorator,
            ScheduledExecutorService schedulerPool,
            JobListener jobListener,
            boolean initiallyPaused,
            BiFunction<ExecutorService, Job, ExecutorService> jobPoolProvider,
            boolean scattering,
            double scatteringRatio
    ) {
        Validate.inclusiveBetween(0.0, 1.0, scatteringRatio);
        this.jobDecorator = jobDecorator;
        this.schedulerPool = schedulerPool;
        this.jobListener = jobListener;
        this.schedulerPaused = new AtomicBoolean(initiallyPaused);
        this.jobPoolProvider = jobPoolProvider;
        this.scattering = scattering;
        this.scatteringRatio = scatteringRatio;
    }

    @Override
    public void schedule(Job job, Schedule schedule) {
        logger.info("[scheduler][job]{} Scheduling with {}", job.getKey(), schedule);
        // Manages existing schedule
        JobScheduledService existingService = services.remove(job.getKey());
        if (existingService != null) {
            logger.info("[scheduler][job]{} Stopping existing schedule", job.getKey());
            existingService.cancel(false);
        }
        // Creates and starts the scheduled service
        logger.info("[scheduler][job]{} Starting scheduled service", job.getKey());
        // Copy stats from old schedule
        JobScheduledService jobScheduledService = new JobScheduledService(
                job,
                schedule,
                schedulerPool,
                existingService,
                jobListener.isPausedAtStartup(job.getKey())
        );
        // Registration
        services.put(job.getKey(), jobScheduledService);
    }

    @Override
    public boolean unschedule(JobKey key) {
        return unschedule(key, true);
    }

    protected boolean unschedule(JobKey key, boolean forceStop) {
        logger.debug("[scheduler][job]{} Unscheduling job", key);
        JobScheduledService existingService = services.remove(key);
        if (existingService != null) {
            logger.debug("[scheduler][job]{} Stopping running job", key);
            existingService.cancel(forceStop);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void pause() {
        schedulerPaused.set(true);
    }

    @Override
    public void resume() {
        schedulerPaused.set(false);
    }

    @Override
    public boolean isPaused() {
        return schedulerPaused.get();
    }

    @Override
    public boolean pause(JobKey key) {
        JobScheduledService existingService = services.get(key);
        if (existingService != null) {
            existingService.pause();
            return true;
        } else {
            throw new JobNotScheduledException(key);
        }
    }

    @Override
    public boolean resume(JobKey key) {
        JobScheduledService existingService = services.get(key);
        if (existingService != null) {
            existingService.resume();
            return true;
        } else {
            throw new JobNotScheduledException(key);
        }
    }

    @Override
    public Optional<JobStatus> getJobStatus(JobKey key) {
        JobScheduledService existingService = services.get(key);
        if (existingService != null) {
            return Optional.of(existingService.getJobStatus());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<JobKey> getJobKey(long id) {
        return services.values().stream()
                .filter(service -> service.getId() == id)
                .map(JobScheduledService::getJobKey)
                .findFirst();
    }

    @Override
    public boolean stop(JobKey key) {
        JobScheduledService existingService = services.get(key);
        if (existingService != null) {
            return existingService.stop();
        } else {
            throw new JobNotScheduledException(key);
        }
    }

    @Override
    public Collection<JobKey> getAllJobKeys() {
        return services.keySet();
    }

    @Override
    public Collection<JobKey> getJobKeysOfType(JobType type) {
        return getAllJobKeys().stream()
                .filter(key -> key.sameType(type))
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<JobKey> getJobKeysOfCategory(JobCategory category) {
        return getAllJobKeys().stream()
                .filter(key -> key.sameCategory(category))
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<JobStatus> getJobStatuses() {
        return services.values().stream()
                .map(JobScheduledService::getJobStatus)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Future<?>> fireImmediately(JobKey jobKey) {
        // Gets the existing scheduled service
        JobScheduledService jobScheduledService = services.get(jobKey);
        if (jobScheduledService == null) {
            throw new JobNotScheduledException(jobKey);
        }
        // Fires the job immediately
        return jobScheduledService.doRun(true);
    }

    protected ExecutorService getExecutorService(Job job) {
        return jobPoolProvider.apply(schedulerPool, job);
    }

    private class JobScheduledService implements Runnable {

        private final long id;
        private final Job job;
        private final Schedule schedule;
        private final ScheduledFuture<?> scheduledFuture;

        private final AtomicBoolean paused;

        private final AtomicReference<Future<?>> currentExecution = new AtomicReference<>();
        private final AtomicReference<JobRunProgress> runProgress = new AtomicReference<>();
        private final AtomicLong runCount = new AtomicLong();
        private final AtomicReference<LocalDateTime> lastRunDate = new AtomicReference<>();
        private final AtomicLong lastRunDurationMs = new AtomicLong();
        private final AtomicLong lastErrorCount = new AtomicLong();
        private final AtomicReference<String> lastError = new AtomicReference<>(null);

        private JobScheduledService(Job job, Schedule schedule, ScheduledExecutorService scheduledExecutorService, JobScheduledService old, boolean pausedAtStartup) {
            this.id = idGenerator.incrementAndGet();
            this.job = job;
            this.schedule = schedule;
            // Paused at startup
            this.paused = new AtomicBoolean(pausedAtStartup);
            if (pausedAtStartup) {
                logger.debug("[job]{} Job paused at startup", job.getKey());
            }
            // Copies stats from old service
            if (old != null) {
                runCount.set(old.runCount.get());
                lastRunDate.set(old.lastRunDate.get());
                lastRunDurationMs.set(old.lastRunDurationMs.get());
                lastErrorCount.set(old.lastErrorCount.get());
                lastError.set(old.lastError.get());
            }
            // Converting all units to milliseconds
            long initialPeriod = TimeUnit.MILLISECONDS.convert(schedule.getInitialPeriod(), schedule.getUnit());
            long period = TimeUnit.MILLISECONDS.convert(schedule.getPeriod(), schedule.getUnit());
            // Scattering
            if (scattering) {
                // Computes the hash for the job key
                int hash = job.getKey().hashCode();
                // Period to consider
                long scatteringMax = (long)(period * scatteringRatio);
                if (scatteringMax > 0) {
                    // Modulo on the period
                    long delay = hash % scatteringMax;
                    logger.debug("[job]{} Scattering enabled - additional delay: {} ms", job.getKey(), delay);
                    // Adding to the initial delay
                    initialPeriod += delay;
                }
            }
            // Scheduling now
            if (schedule.getPeriod() > 0) {
                scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(
                        this,
                        initialPeriod,
                        period,
                        TimeUnit.MILLISECONDS
                );
            } else {
                logger.debug("[job]{} Job not scheduled since period = 0", job.getKey());
                scheduledFuture = null;
            }
        }

        public long getId() {
            return id;
        }

        public JobKey getJobKey() {
            return job.getKey();
        }

        @Override
        public void run() {
            if (!schedulerPaused.get()) {
                doRun(false);
            }
        }

        protected Optional<Future<?>> doRun(boolean force) {
            logger.debug("[job][run]{} Trying to run now - forced = {}", job.getKey(), force);
            if (job.isValid()) {
                if (job.isDisabled()) {
                    logger.debug("[job][run]{} Not allowed to run now because disabled", job.getKey());
                    return Optional.empty();
                } else if (paused.get() && !force) {
                    logger.debug("[job][run]{} Not allowed to run now because paused", job.getKey());
                    return Optional.empty();
                } else if (currentExecution.get() != null) {
                    logger.debug("[job][run]{} Not allowed to run now because already running", job.getKey());
                    return Optional.empty();
                } else {
                    // Task to run
                    Runnable run = getRun();
                    // Gets the executor for this job
                    ExecutorService executor = getExecutorService(job);
                    // Scheduling
                    logger.debug("[job][run]{} Job task submitted asynchronously", job.getKey());
                    Future<?> execution = executor.submit(run);
                    currentExecution.set(execution);
                    return Optional.of(execution);
                }
            } else {
                logger.debug("[job][run]{} Not valid - removing from schedule", job.getKey());
                unschedule(job.getKey(), false);
                return Optional.empty();
            }
        }

        private Runnable getRun() {
            JobRunListener jobRunListener = new DefaultJobRunListener();
            // Initial task
            Runnable rootTask = () -> job.getTask().run(jobRunListener);
            // Decorated task
            Runnable decoratedTask = jobDecorator.decorate(job, rootTask);
            // Run indicator
            Runnable runnable = new MonitoredRun(decoratedTask, new MonitoredRunListenerAdapter() {
                @Override
                public void onCompletion() {
                    logger.debug("[job][task]{} Removed job execution", job.getKey());
                    currentExecution.set(null);
                }
            });
            // Monitoring the run
            MonitoredRunListener monitoredRunListener = new MonitoredRunListener() {
                @Override
                public void onStart() {
                    logger.debug("[job][task]{} On start", job.getKey());
                    lastRunDate.set(Time.now());
                    runCount.incrementAndGet();
                    // Starting
                    jobListener.onJobStart(job.getKey());
                }

                @Override
                public void onSuccess(long duration) {
                    lastRunDurationMs.set(duration);
                    logger.debug("[job][task]{} Success in {} ms", job.getKey(), duration);
                    // Starting
                    jobListener.onJobEnd(job.getKey(), duration);
                    // No error - resetting the counters
                    lastErrorCount.set(0);
                    lastError.set(null);
                }

                @Override
                public void onFailure(Exception ex) {
                    lastErrorCount.incrementAndGet();
                    lastError.set(ex.getMessage());
                    // Only writing the error in debug mode, we count on the job listener
                    // to log the error properly
                    logger.debug("[job][task]{} Failure: {}", job.getKey(), ex.getMessage());
                    // Reporter
                    jobListener.onJobError(getJobStatus(), ex);
                }

                @Override
                public void onCompletion() {
                    runProgress.set(null);
                    logger.debug("[job][task]{} Job completed.", job.getKey());
                    // Starting
                    jobListener.onJobComplete(job.getKey());
                }
            };
            // Monitored task
            return new MonitoredRun(runnable, monitoredRunListener);
        }

        public boolean stop() {
            logger.debug("[job]{} Stopping job", job.getKey());
            return currentExecution.updateAndGet(
                    current -> {
                        if (current != null) {
                            current.cancel(true);
                        }
                        return null;
                    }
            ) == null;
        }

        public boolean cancel(boolean forceStop) {
            logger.debug("[job]{} Cancelling job (forcing = {})", job.getKey(), forceStop);
            if (forceStop) {
                stop();
            }
            return scheduledFuture != null && scheduledFuture.cancel(forceStop);
        }

        public JobStatus getJobStatus() {
            boolean valid = job.isValid();
            return new JobStatus(
                    id,
                    job.getKey(),
                    schedule,
                    job.getDescription(),
                    currentExecution.get() != null,
                    valid,
                    paused.get(),
                    job.isDisabled(),
                    runProgress.get(),
                    runCount.get(),
                    lastRunDate.get(),
                    lastRunDurationMs.get(),
                    getNextRunDate(valid),
                    lastErrorCount.get(),
                    lastError.get()
            );
        }

        private LocalDateTime getNextRunDate(boolean valid) {
            if (valid && scheduledFuture != null) {
                return Time.now().plus(
                        scheduledFuture.getDelay(TimeUnit.SECONDS),
                        ChronoUnit.SECONDS
                );
            } else {
                return null;
            }
        }

        public void pause() {
            if (scheduledFuture != null) {
                paused.set(true);
                jobListener.onJobPaused(job.getKey());
            }
        }

        public void resume() {
            if (scheduledFuture != null) {
                paused.set(false);
                jobListener.onJobResumed(job.getKey());
            }
        }

        private class DefaultJobRunListener implements JobRunListener {

            @Override
            public void progress(JobRunProgress progress) {
                jobListener.onJobProgress(job.getKey(), progress);
                logger.debug("[job][progress]{} {}",
                        job.getKey(),
                        progress.getText()
                );
                runProgress.set(progress);
            }

        }

    }
}
