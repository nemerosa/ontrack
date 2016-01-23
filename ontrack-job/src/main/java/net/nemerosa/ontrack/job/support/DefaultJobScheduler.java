package net.nemerosa.ontrack.job.support;

import net.nemerosa.ontrack.job.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class DefaultJobScheduler implements JobScheduler {

    private final Logger logger = LoggerFactory.getLogger(JobScheduler.class);

    private final JobDecorator jobDecorator;
    private final ScheduledExecutorService scheduledExecutorService;

    private final Map<JobKey, JobScheduledService> services = new ConcurrentHashMap<>(new TreeMap<>());

    public DefaultJobScheduler(JobDecorator jobDecorator, ScheduledExecutorService scheduledExecutorService) {
        this.jobDecorator = jobDecorator;
        this.scheduledExecutorService = scheduledExecutorService;
    }

    @Override
    public void schedule(Job job, Schedule schedule) {
        logger.info("[job] Scheduling {} with schedule {}", job.getKey(), schedule);
        // Manages existing schedule
        JobScheduledService existingService = services.remove(job.getKey());
        if (existingService != null) {
            logger.info("[job] Stopping existing service for {}", job.getKey());
            existingService.cancel();
        }
        // Gets the job task
        Runnable jobTask = job.getTask();
        // Decorates the task
        Runnable decoratedTask = jobDecorator.decorate(job, jobTask);
        // Creates and starts the scheduled service
        logger.info("[job] Starting service {}", job.getKey());
        // TODO Copy stats from old schedule
        JobScheduledService jobScheduledService = new JobScheduledService(job, decoratedTask, schedule, scheduledExecutorService);
        // Registration
        services.put(job.getKey(), jobScheduledService);
    }

    @Override
    public JobStatus getJobStatus(JobKey key) {
        JobScheduledService existingService = services.get(key);
        if (existingService != null) {
            return existingService.getJobStatus();
        } else {
            throw new JobNotScheduledException(key);
        }
    }

    @Override
    public Collection<JobStatus> getJobStatuses() {
        return services.values().stream()
                .map(JobScheduledService::getJobStatus)
                .collect(Collectors.toList());
    }

    @Override
    public Future<?> fireImmediately(JobKey jobKey) {
        // Gets the existing scheduled service
        JobScheduledService jobScheduledService = services.get(jobKey);
        if (jobScheduledService == null) {
            throw new JobNotScheduledException(jobKey);
        }
        // Fires the job immediately
        return jobScheduledService.fireImmediately();
    }

    private class JobScheduledService implements Runnable {

        private final Job job;
        private final Runnable decoratedTask;
        private final ScheduledFuture<?> scheduledFuture;

        private AtomicReference<CompletableFuture<?>> completableFuture = new AtomicReference<>();

        private JobScheduledService(Job job, Runnable decoratedTask, Schedule schedule, ScheduledExecutorService scheduledExecutorService) {
            this.job = job;
            this.decoratedTask = decoratedTask;
            scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(
                    this,
                    schedule.getInitialPeriod(),
                    schedule.getPeriod(),
                    schedule.getUnit()
            );
        }

        @Override
        public void run() {
            fireImmediately();
        }

        public void cancel() {
            scheduledFuture.cancel(false);
            // The decorated task might still run
        }

        public Future<?> fireImmediately() {
            return completableFuture.updateAndGet(this::optionallyFireTask);
        }

        protected CompletableFuture<?> optionallyFireTask(CompletableFuture<?> runningCompletableFuture) {
            if (runningCompletableFuture != null) {
                return runningCompletableFuture;
            } else {
                return fireTask();
            }
        }

        protected CompletableFuture<Void> fireTask() {
            return CompletableFuture
                    .runAsync(decoratedTask, scheduledExecutorService)
                    .whenComplete((ignored, ex) -> {
                        completableFuture.set(null);
                        // TODO Stores the exception
                    });
        }

        public JobStatus getJobStatus() {
            return new JobStatus(
                    job.getKey(),
                    job.getDescription(),
                    completableFuture.get() != null,
                    0, // TODO Run count
                    null, // TODO Last run date
                    0, // TODO Duration of last run
                    null, // TODO Next execution
                    0, // TODO Last error count
                    null // TODO Last error message
            );
        }
    }
}
