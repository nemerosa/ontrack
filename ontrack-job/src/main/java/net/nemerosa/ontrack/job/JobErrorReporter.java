package net.nemerosa.ontrack.job;

public interface JobErrorReporter {

    void onJobError(JobStatus jobStatus, Exception ex);

}
