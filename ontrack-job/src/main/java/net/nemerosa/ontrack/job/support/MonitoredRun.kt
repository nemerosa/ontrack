package net.nemerosa.ontrack.job.support

/**
 * Embeds another task into monitoring activities.
 */
class MonitoredRun(private val embedded: Runnable, private val runListener: MonitoredRunListener) : Runnable {

    override fun run() {
        try {
            runListener.onStart()
            // Runs the job
            val start = System.currentTimeMillis()
            embedded.run()
            // No error, counting time
            val end = System.currentTimeMillis()
            runListener.onSuccess(end - start)
        } catch (ex: Exception) {
            runListener.onFailure(ex)
            // Rethrows the error
            throw ex
        } finally {
            runListener.onCompletion()
        }
    }
}
