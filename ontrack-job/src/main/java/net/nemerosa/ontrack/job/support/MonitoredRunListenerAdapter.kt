package net.nemerosa.ontrack.job.support

open class MonitoredRunListenerAdapter : MonitoredRunListener {
    override fun onStart() {}

    override fun onSuccess(duration: Long) {}

    override fun onFailure(ex: Exception) {}

    override fun onCompletion() {}
}

