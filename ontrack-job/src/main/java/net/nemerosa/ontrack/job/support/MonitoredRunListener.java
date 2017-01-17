package net.nemerosa.ontrack.job.support;

/**
 * Listens to the execution of a task.
 */
public interface MonitoredRunListener {

    /**
     * When the task starts.
     */
    void onStart();

    /**
     * When the tasks has finished without error
     *
     * @param duration Duration of the execution in milliseconds.
     */
    void onSuccess(long duration);

    /**
     * When the task has exited with an exception.
     *
     * @param ex The exception returned by the task.
     */
    void onFailure(Exception ex);

    /**
     * When the task is finished, either on a success or on a failure.
     */
    void onCompletion();
}
