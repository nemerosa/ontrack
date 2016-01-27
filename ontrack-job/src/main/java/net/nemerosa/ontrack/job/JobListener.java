package net.nemerosa.ontrack.job;

public interface JobListener {

    void onJobStart(JobKey key);

    void onJobEnd(JobKey key, long milliseconds);

    void onJobError(JobKey key, Exception ex);

    void onJobComplete(JobKey key);

    void onJobProgress(JobKey key, JobRunProgress progress);

}
