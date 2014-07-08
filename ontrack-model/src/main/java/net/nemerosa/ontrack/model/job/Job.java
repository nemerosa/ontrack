package net.nemerosa.ontrack.model.job;

/**
 * Defines a job that can run asynchronously.
 */
public interface Job {

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
                getCategory(),
                getId(),
                getDescription(),
                getInterval()
        );
    }

}
