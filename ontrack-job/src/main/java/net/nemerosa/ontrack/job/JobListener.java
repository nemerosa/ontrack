package net.nemerosa.ontrack.job;

public interface JobListener {

    void onJobStart(JobKey key);

    void onJobPaused(JobKey key);

    void onJobResumed(JobKey key);

    void onJobEnd(JobKey key, long milliseconds);

    void onJobError(JobStatus key, Exception ex);

    void onJobComplete(JobKey key);

    void onJobProgress(JobKey key, JobRunProgress progress);

}
