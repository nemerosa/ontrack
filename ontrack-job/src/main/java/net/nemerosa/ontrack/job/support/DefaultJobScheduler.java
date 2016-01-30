package net.nemerosa.ontrack.job.support;

import net.nemerosa.ontrack.common.Time;
import net.nemerosa.ontrack.job.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;
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
    private final AtomicBoolean schedulerPaused = new AtomicBoolean(false);

    private final AtomicLong idGenerator = new AtomicLong();

    public DefaultJobScheduler(JobDecorator jobDecorator, ScheduledExecutorService scheduledExecutorService, JobListener jobListener) {
        this.jobDecorator = jobDecorator;
        this.scheduledExecutorService = scheduledExecutorService;
        this.jobListener = jobListener;
    }

    @Override
    public void schedule(Job job, Schedule schedule) {
        logger.info("[job][{}][{}] Scheduling with {}", job.getKey().getType().getKey(), job.getKey().getId(), schedule);
        // Manages existing schedule
        JobScheduledService existingService = services.remove(job.getKey());
        if (existingService != null) {
            logger.info("[job][{}][{}] Stopping existing schedule", job.getKey().getType().getKey(), job.getKey().getId());
            existingService.cancel(false);
        }
        // Creates and starts the scheduled service
        logger.info("[job][{}][{}] Starting service", job.getKey().getType().getKey(), job.getKey().getId());
        // Copy stats from old schedule
        JobScheduledService jobScheduledService = new JobScheduledService(job, schedule, scheduledExecutorService, existingService);
        // Registration
        services.put(job.getKey(), jobScheduledService);
    }

    @Override
    public void unschedule(JobKey key) {
        JobScheduledService existingService = services.remove(key);
        if (existingService != null) {
            existingService.cancel(true);
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
    public Future<?> fireImmediately(JobKey jobKey) {
        return fireImmediately(jobKey, Collections.emptyMap());
    }

    @Override
    public Future<?> fireImmediately(JobKey jobKey, Map<String, ?> parameters) {
        // Gets the existing scheduled service
        JobScheduledService jobScheduledService = services.get(jobKey);
        if (jobScheduledService == null) {
            throw new JobNotScheduledException(jobKey);
        }
        // Fires the job immediately
        return jobScheduledService.fireImmediately(parameters);
    }

    private class JobScheduledService implements Runnable {

        private final long id;
        private final Job job;
        private final Schedule schedule;
        private final Runnable monitoredTask;
        private final ScheduledFuture<?> scheduledFuture;

        private final AtomicBoolean paused = new AtomicBoolean(false);

        private final AtomicReference<Map<String, ?>> runParameters = new AtomicReference<>();
        private final AtomicReference<CompletableFuture<?>> completableFuture = new AtomicReference<>();

        private final AtomicReference<JobRunProgress> runProgress = new AtomicReference<>();
        private final AtomicLong runCount = new AtomicLong();
        private final AtomicReference<LocalDateTime> lastRunDate = new AtomicReference<>();
        private final AtomicLong lastRunDurationMs = new AtomicLong();
        private final AtomicLong lastErrorCount = new AtomicLong();
        private final AtomicReference<String> lastError = new AtomicReference<>(null);

        private JobScheduledService(Job job, Schedule schedule, ScheduledExecutorService scheduledExecutorService, JobScheduledService old) {
            this.id = idGenerator.incrementAndGet();
            this.job = job;
            this.schedule = schedule;
            this.monitoredTask = jobDecorator.decorate(job, new MonitoredTask());
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
        }

        public long getId() {
            return id;
        }

        public JobKey getJobKey() {
            return job.getKey();
        }

        @Override
        public void run() {
            fireImmediately(Collections.emptyMap());
        }

        public void cancel(boolean forceStop) {
            if (scheduledFuture != null) {
                scheduledFuture.cancel(false);
            }
            // The decorated task might still run
            CompletableFuture<?> future = this.completableFuture.get();
            if (forceStop && future != null && !future.isDone() && !future.isCancelled()) {
                future.cancel(true);
            }
        }

        public Future<?> fireImmediately(Map<String, ?> parameters) {
            return completableFuture.updateAndGet(cf -> optionallyFireTask(cf, parameters));
        }

        protected CompletableFuture<?> optionallyFireTask(CompletableFuture<?> runningCompletableFuture, Map<String, ?> parameters) {
            if (runningCompletableFuture != null) {
                return runningCompletableFuture;
            } else {
                return fireTask(parameters);
            }
        }

        protected CompletableFuture<Void> fireTask(Map<String, ?> parameters) {
            runParameters.set(parameters);
            return CompletableFuture
                    .runAsync(monitoredTask, scheduledExecutorService)
                    .whenComplete((ignored, ex) -> completableFuture.set(null));
        }

        public JobStatus getJobStatus() {
            boolean valid = job.isValid();
            return new JobStatus(
                    id,
                    job.getKey(),
                    schedule,
                    job.getDescription(),
                    completableFuture.get() != null,
                    valid,
                    paused.get() || schedulerPaused.get(),
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
            paused.set(true);
        }

        public void resume() {
            paused.set(false);
        }

        private class DefaultJobRunListener implements JobRunListener {

            @Override
            public void progress(JobRunProgress progress) {
                jobListener.onJobProgress(job.getKey(), progress);
                logger.debug("[job][{}][{}] {}",
                        job.getKey().getType().getKey(),
                        job.getKey().getId(),
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

            @Override
            public void run() {
                if (job.isValid()) {
                    if (isEnabled()) {
                        try {
                            logger.debug("[job][{}][{}] Running now", job.getKey().getType().getKey(), job.getKey().getId());
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
                            logger.debug("[job][{}][{}] Ran in {} ms", job.getKey().getType().getKey(), job.getKey().getId(), lastRunDurationMs.get());
                            // Starting
                            jobListener.onJobEnd(job.getKey(), lastRunDurationMs.get());
                            // No error - resetting the counters
                            lastErrorCount.set(0);
                            lastError.set(null);
                        } catch (Exception ex) {
                            lastErrorCount.incrementAndGet();
                            lastError.set(ex.getMessage());
                            logger.error("[job][{}][{}] Error: {}", job.getKey().getType().getKey(), job.getKey().getId(), ex.getMessage());
                            // Reporter
                            logger.error(
                                    String.format("[job][%s][%s] Error", job.getKey().getType().getKey(), job.getKey().getId()),
                                    ex
                            );
                            jobListener.onJobError(job.getKey(), ex);
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
                        logger.debug("[job][{}][{}] Not enabled", job.getKey().getType().getKey(), job.getKey().getId());
                    }
                } else {
                    logger.debug("[job]{} Not valid - removing from schedule", job.getKey());
                    unschedule(job.getKey());
                }
            }
        }

        private boolean isEnabled() {
            return !job.isDisabled() && !paused.get() && !schedulerPaused.get();
        }
    }
}
