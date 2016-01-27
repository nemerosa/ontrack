package net.nemerosa.ontrack.model.job;

/**
 * Defines a job that can run asynchronously.
 */
@Deprecated
public interface Job {

    /**
     * Hour interval
     */
    int HOUR = 60;

    /**
     * Day interval
     */
    int DAY = HOUR * 24;

    /**
     * Job group, defaults to the category
     */
    default String getGroup() {
        return getCategory();
    }

    /**
     * Job category
     */
    String getCategory();

    /**
     * Unique ID for this job in the category.
     */
    String getId();

    /**
     * Description for this jobs
     */
    String getDescription();

    /**
     * State of the job
     */
    boolean isDisabled();

    /**
     * Interval (in minutes) between each run
     */
    int getInterval();

    /**
     * Gets the actual task to run
     */
    JobTask createTask();

    /**
     * Descriptor for this job.
     */
    default JobDescriptor getDescriptor() {
        return new JobDescriptor(
                getGroup(),
                getCategory(),
                getId(),
                getDescription(),
                isDisabled(),
                getInterval()
        );
    }

}
