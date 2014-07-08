package net.nemerosa.ontrack.service.job;

import net.nemerosa.ontrack.model.job.Job;
import net.nemerosa.ontrack.model.job.JobDescriptor;
import net.nemerosa.ontrack.model.support.Time;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class RegisteredJob {

    private Job job;
    private long sync;

    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicLong runCount = new AtomicLong();
    private final AtomicReference<LocalDateTime> end = new AtomicReference<>(null);

    protected RegisteredJob(Job job, long sync) {
        this.job = job;
        this.sync = sync;
    }

    public void sync(Job job, long sync) {
        this.job = job;
        this.sync = sync;
    }

    public static RegisteredJob of(Job job, long count) {
        return new RegisteredJob(job, count);
    }

    public boolean checkSync(long count) {
        return sync >= count;
    }

    public boolean isRunning() {
        return running.get();
    }

    public boolean mustStart() {
        if (end.get() != null) {
            long minutes = Duration.between(end.get(), Time.now()).toMinutes();
            return minutes >= job.getInterval();
        } else {
            return runCount.get() == 0;
        }
    }

    public Runnable createTask() {
        return () -> {
            try {
                // Starting
                end.set(null);
                running.set(true);
                runCount.incrementAndGet();
                // Runs the job
                job.createTask().run();
            } finally {
                // End
                running.set(false);
                end.set(Time.now());
            }
        };
    }

    @Override
    public String toString() {
        return String.format(
                "[%s/%s] %s",
                job.getCategory(),
                job.getId(),
                job.getDescription()
        );
    }

    public String getJobCategory() {
        return job.getCategory();
    }

    public String getJobDescription() {
        return job.getDescription();
    }

    public JobDescriptor getJobDescriptor() {
        return job.getDescriptor();
    }
}
