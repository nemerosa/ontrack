package net.nemerosa.ontrack.job.support;

import net.nemerosa.ontrack.job.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

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
        JobScheduledService jobScheduledService = new JobScheduledService(decoratedTask, schedule, scheduledExecutorService);
        // Starts the service
        // Registration
        services.put(job.getKey(), jobScheduledService);
    }


    private class JobScheduledService implements Runnable {

        private final Runnable decoratedTask;
        private final ScheduledFuture<?> scheduledFuture;

        private JobScheduledService(Runnable decoratedTask, Schedule schedule, ScheduledExecutorService scheduledExecutorService) {
            this.decoratedTask = decoratedTask;
            scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(
                    this,
                    0,
                    schedule.getPeriod(),
                    schedule.getUnit()
            );
        }

        @Override
        public void run() {
            decoratedTask.run();
        }

        public void cancel() {
            scheduledFuture.cancel(false);
            // The decorated task might still run
        }
    }
}
