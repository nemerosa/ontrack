package net.nemerosa.ontrack.job.support

/**
 * Listens to the execution of a task.
 */
interface MonitoredRunListener {

    /**
     * When the task starts.
     */
    fun onStart()

    /**
     * When the tasks has finished without error
     *
     * @param duration Duration of the execution in milliseconds.
     */
    fun onSuccess(duration: Long)

    /**
     * When the task has exited with an exception.
     *
     * @param ex The exception returned by the task.
     */
    fun onFailure(ex: Exception)

    /**
     * When the task is finished, either on a success or on a failure.
     */
    fun onCompletion()
}
