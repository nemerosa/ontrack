package net.nemerosa.ontrack.job;

import java.util.function.Consumer;

public class OutputJobListener implements JobListener {

    private final Consumer<String> output;

    public OutputJobListener(Consumer<String> output) {
        this.output = output;
    }

    @Override
    public void onJobStart(JobKey key) {
        output.accept(String.format("job.start %s", key));
    }

    @Override
    public void onJobEnd(JobKey key, long milliseconds) {
        output.accept(String.format("job.end %s %dms", key, milliseconds));
    }

    @Override
    public void onJobError(JobStatus status, Exception ex) {
        output.accept(String.format("job.error %s %s", status.getKey(), ex.getMessage()));
    }

    @Override
    public void onJobComplete(JobKey key) {
        output.accept(String.format("job.complete %s", key));
    }

    @Override
    public void onJobProgress(JobKey key, JobRunProgress progress) {
        output.accept(String.format("job.progress %s %s", key, progress));
    }

    @Override
    public boolean isPausedAtStartup(JobKey key) {
        return false;
    }

    @Override
    public void onJobPaused(JobKey key) {
        output.accept(String.format("job.paused %s", key));
    }

    @Override
    public void onJobResumed(JobKey key) {
        output.accept(String.format("job.resumed %s", key));
    }

}
