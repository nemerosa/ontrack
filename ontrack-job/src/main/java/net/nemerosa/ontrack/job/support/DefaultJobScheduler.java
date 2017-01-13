package net.nemerosa.ontrack.job.support;

import net.nemerosa.ontrack.common.Time;
import net.nemerosa.ontrack.job.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class DefaultJobScheduler implements JobScheduler {

    private final Logger logger = LoggerFactory.getLogger(JobScheduler.class);

    private final JobDecorator jobDecorator;
    private final ScheduledExecutorService scheduledExecutorService;
    private final JobListener jobListener;

    private final Map<JobKey, JobScheduledService> services = new ConcurrentHashMap<>(new TreeMap<>());
    private final AtomicBoolean schedulerPaused;

    private final AtomicLong idGenerator = new AtomicLong();

    public DefaultJobScheduler(
            JobDecorator jobDecorator,
            ScheduledExecutorService scheduledExecutorService,
            JobListener jobListener,
            boolean initiallyPaused
    ) {
        this.jobDecorator = jobDecorator;
        this.scheduledExecutorService = scheduledExecutorService;
        this.jobListener = jobListener;
        this.schedulerPaused = new AtomicBoolean(initiallyPaused);
    }

    @Override
    public void schedule(Job job, Schedule schedule) {
        logger.info("[job]{} Scheduling with {}", job.getKey(), schedule);
        // Manages existing schedule
        JobScheduledService existingService = services.remove(job.getKey());
        if (existingService != null) {
            logger.info("[job]{} Stopping existing schedule", job.getKey());
            existingService.cancel(false);
        }
        // Creates and starts the scheduled service
        logger.info("[job]{} Starting service", job.getKey());
        // Copy stats from old schedule
        JobScheduledService jobScheduledService = new JobScheduledService(
                job,
                schedule,
                scheduledExecutorService,
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
        logger.debug("[job]{} Unscheduling job", key);
        JobScheduledService existingService = services.remove(key);
        if (existingService != null) {
            logger.debug("[job]{} Stopping running job", key);
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
            return existingService.cancel(true);
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
    public ListenableFuture<?> fireImmediately(JobKey jobKey) {
        return fireImmediately(jobKey, Collections.emptyMap());
    }

    @Override
    public ListenableFuture<?> fireImmediately(JobKey jobKey, Map<String, ?> parameters) {
        // Gets the existing scheduled service
        JobScheduledService jobScheduledService = services.get(jobKey);
        if (jobScheduledService == null) {
            throw new JobNotScheduledException(jobKey);
        }
        // Fires the job immediately
        return jobScheduledService.fireImmediately(true, parameters);
    }

    @Override
    public ListenableFuture<?> runOnce(Job job) {
        JobKey key = job.getKey();
        logger.info("[job]*{} Scheduling job after unique run", key);
        // Registers the job, without any schedule
        schedule(job, Schedule.NONE);
        // Fires it immedietely
        ListenableFuture<?> future = fireImmediately(key);
        // Unscheduling
        Runnable unscheduling = () -> {
            logger.info("[job]*{} Unscheduling job after unique run", key);
            unschedule(key);
        };
        // On completion, unschedules the job
        future.addCallback(
                result -> unscheduling.run(),
                ex -> unscheduling.run()
        );
        return future;
    }

    private class JobScheduledService implements Runnable {

        private final long id;
        private final Job job;
        private final Schedule schedule;
        private final ScheduledFuture<?> scheduledFuture;

        private final AtomicBoolean paused;

        private final AtomicReference<Map<String, ?>> runParameters = new AtomicReference<>();
        private final AtomicReference<ListenableFuture<?>> currentExecution = new AtomicReference<>();

        private final AtomicReference<JobRunProgress> runProgress = new AtomicReference<>();
        private final AtomicLong runCount = new AtomicLong();
        private final AtomicReference<LocalDateTime> lastRunDate = new AtomicReference<>();
        private final AtomicLong lastRunDurationMs = new AtomicLong();
        private final AtomicLong lastErrorCount = new AtomicLong();
        private final AtomicReference<String> lastError = new AtomicReference<>(null);

        private final AsyncListenableTaskExecutor listenableTaskExecutor;

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
            // Scheduling now
            if (schedule.getPeriod() > 0) {
                scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(
                        this,
                        schedule.getInitialPeriod(),
                        schedule.getPeriod(),
                        schedule.getUnit()
                );
            } else {
                logger.debug("[job]{} Job not scheduled since period = 0", job.getKey());
                scheduledFuture = null;
            }
            // Task executor
            listenableTaskExecutor = new ConcurrentTaskExecutor(scheduledExecutorService);
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
                fireImmediately(false, Collections.emptyMap());
            }
        }

        public boolean cancel(boolean forceStop) {
            if (scheduledFuture != null) {
                logger.debug("[job]{} Cancelling schedule", job.getKey());
                scheduledFuture.cancel(false);
            }
            // The decorated task might still run
            ListenableFuture<?> future = this.currentExecution.get();
            return forceStop &&
                    future != null &&
                    !future.isDone() &&
                    !future.isCancelled() &&
                    future.cancel(true);
        }

        public ListenableFuture<?> fireImmediately(boolean force, Map<String, ?> parameters) {
            return currentExecution.updateAndGet(cf -> optionallyFireTask(cf, force, parameters));
        }

        protected ListenableFuture<?> optionallyFireTask(ListenableFuture<?> runningCompletableFuture, boolean force, Map<String, ?> parameters) {
            if (runningCompletableFuture != null) {
                logger.debug("[job]{} Returning already running job", job.getKey());
                /*
                 * If the task is already running, we do not run it in concurrency,
                 * even if force is set to true
                 */
                return runningCompletableFuture;
            } else {
                return fireTask(force, parameters);
            }
        }

        protected ListenableFuture<?> fireTask(boolean force, Map<String, ?> parameters) {
            runParameters.set(parameters);
            // Task to run
            MonitoredTask monitoredTask = new MonitoredTask(force);
            // Decorates this task
            Runnable decoratedTask = jobDecorator.decorate(job, monitoredTask);
            // Runs it
            ListenableFuture<?> listenableFuture = listenableTaskExecutor.submitListenable(decoratedTask);
            // Unsetting on completion
            listenableFuture.addCallback(
                    result -> currentExecution.set(null),
                    ex -> currentExecution.set(null)
            );
            // OK
            return listenableFuture;
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
                    runParameters.get(),
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
                logger.debug("[job]{} {}",
                        job.getKey(),
                        progress.getText()
                );
                runProgress.set(progress);
            }

            @Override
            public <T> Optional<T> getParam(String key) {
                Map<String, ?> parameters = runParameters.get();
                if (parameters != null) {
                    @SuppressWarnings("unchecked")
                    T t = (T) parameters.get(key);
                    return Optional.ofNullable(t);
                } else {
                    return Optional.empty();
                }
            }
        }

        private class MonitoredTask implements Runnable {

            private final boolean force;

            private MonitoredTask(boolean force) {
                this.force = force;
            }

            @Override
            public void run() {
                logger.debug("[job]{} Trying to run now - forced = {}", job.getKey(), force);
                if (job.isValid()) {
                    if (canRunNow(force)) {
                        try {
                            logger.debug("[job]{} Running now", job.getKey());
                            lastRunDate.set(Time.now());
                            runCount.incrementAndGet();
                            // Starting
                            jobListener.onJobStart(job.getKey());
                            // Runs the job
                            long _start = System.currentTimeMillis();
                            job.getTask().run(new DefaultJobRunListener());
                            // No error, counting time
                            long _end = System.currentTimeMillis();
                            lastRunDurationMs.set(_end - _start);
                            logger.debug("[job]{} Ran in {} ms", job.getKey(), lastRunDurationMs.get());
                            // Starting
                            jobListener.onJobEnd(job.getKey(), lastRunDurationMs.get());
                            // No error - resetting the counters
                            lastErrorCount.set(0);
                            lastError.set(null);
                        } catch (Exception ex) {
                            lastErrorCount.incrementAndGet();
                            lastError.set(ex.getMessage());
                            logger.error("[job]{} Error: {}", job.getKey(), ex.getMessage());
                            // Reporter
                            jobListener.onJobError(getJobStatus(), ex);
                            // Rethrows the error
                            throw ex;
                        } finally {
                            runProgress.set(null);
                            // Removes any parameter
                            runParameters.set(null);
                            // Starting
                            jobListener.onJobComplete(job.getKey());
                        }
                    } else {
                        logger.debug("[job]{} Not allowed to run now", job.getKey());
                    }
                } else {
                    logger.debug("[job]{} Not valid - removing from schedule", job.getKey());
                    unschedule(job.getKey(), false);
                }
            }
        }

        /**
         * A job can run if:
         * <p>
         * * not disabled
         * * AND (forced OR not paused)
         */
        private boolean canRunNow(boolean force) {
            return !job.isDisabled() && (!paused.get() || force);
        }
    }
}
