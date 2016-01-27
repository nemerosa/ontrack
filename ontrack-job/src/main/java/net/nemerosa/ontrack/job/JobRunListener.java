package net.nemerosa.ontrack.job;

@FunctionalInterface
public interface JobRunListener {

    void progress(JobRunProgress value);

}
