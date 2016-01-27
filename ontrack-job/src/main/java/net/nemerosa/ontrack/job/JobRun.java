package net.nemerosa.ontrack.job;

@FunctionalInterface
public interface JobRun {

    void run(JobRunListener runListener);

}
