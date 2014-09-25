package net.nemerosa.ontrack.service.job;

import net.nemerosa.ontrack.model.job.Job;
import net.nemerosa.ontrack.model.job.JobDescriptor;
import net.nemerosa.ontrack.model.job.JobTask;
import net.nemerosa.ontrack.model.support.ApplicationInfo;
import net.nemerosa.ontrack.model.support.Time;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class RegisteredJob {

    private static final AtomicLong ids = new AtomicLong();

    private final long id = ids.incrementAndGet();
    private Job job;
    private long sync;

    private final AtomicReference<JobTask> run = new AtomicReference<>(null);
    private final AtomicLong runCount = new AtomicLong();
    private final AtomicReference<LocalDateTime> lastRunDate = new AtomicReference<>();
    private final AtomicReference<LocalDateTime> end = new AtomicReference<>(null);
    private final AtomicLong lastRunDurationMs = new AtomicLong();

    protected RegisteredJob(Job job, long sync) {
        this.job = job;
        this.sync = sync;
    }

    public long getId() {
        return id;
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
        return run.get() != null;
    }

    public boolean mustStart() {
        if (sync < 0) {
            // Special case of job registered on the fly, outside of normal registration
            return true;
        } else if (end.get() != null) {
            long minutes = Duration.between(end.get(), Time.now()).toMinutes();
            return minutes >= job.getInterval();
        } else {
            return runCount.get() == 0;
        }
    }

    public Runnable createTask() {
        return () -> {
            try {
                // Task
                JobTask task = job.createTask();
                run.set(task);
                // Starting
                end.set(null);
                lastRunDate.set(Time.now());
                runCount.incrementAndGet();
                // Runs the job
                long _start = System.currentTimeMillis();
                try {
                    task.run();
                } finally {
                    long _end = System.currentTimeMillis();
                    lastRunDurationMs.set(_end - _start);
                }
            } finally {
                // End
                run.set(null);
                end.set(Time.now());
            }
        };
    }

    @Override
    public String toString() {
        return String.format(
                "[%s/%s][%s] %s",
                job.getGroup(),
                job.getId(),
                job.getCategory(),
                job.getDescription()
        );
    }

    public String getJobCategory() {
        return job.getCategory();
    }

    public String getJobDescription() {
        return job.getDescription();
    }

    public String getJobGroup() {
        return job.getGroup();
    }

    public String getJobId() {
        return job.getId();
    }

    public JobDescriptor getJobDescriptor() {
        return job.getDescriptor();
    }

    public ApplicationInfo getApplicationInfo() {
        return Optional.ofNullable(run.get()).map(task -> ApplicationInfo.info(task.getInfo())).orElse(null);
    }

    public long getRunCount() {
        return runCount.get();
    }

    public LocalDateTime getLastRunDate() {
        return lastRunDate.get();
    }

    public LocalDateTime getNextRunDate() {
        LocalDateTime date = getLastRunDate();
        if (date != null) {
            return date.plusMinutes(job.getInterval());
        } else {
            return null;
        }
    }

    public long getLastRunDurationMs() {
        return lastRunDurationMs.get();
    }
}
