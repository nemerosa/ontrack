package net.nemerosa.ontrack.job;

public class NOPJobListener implements JobListener {

    public static final JobListener INSTANCE = new NOPJobListener();

    @Override
    public void onJobStart(JobKey key) {
    }

    @Override
    public void onJobEnd(JobKey key, long milliseconds) {
    }

    @Override
    public void onJobError(JobKey key, Exception ex) {
    }

    @Override
    public void onJobComplete(JobKey key) {
    }
}
