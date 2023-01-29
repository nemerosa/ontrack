package net.nemerosa.ontrack.extension.jenkins.notifications

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APILabel

/**
 * Configuration for the notification of a Jenkins job.
 */
data class JenkinsNotificationChannelConfig(
    @APIDescription("Name of the Jenkins configuration to use for the connection.")
    @APILabel("Configuration")
    val config: String,
    @APIDescription("URL of the Jenkins job to call")
    @APILabel("Job")
    val job: String,
    @APIDescription("Parameters to send to to the job")
    @APILabel("Parameters")
    val parameters: List<JenkinsNotificationChannelConfigParam>,
    @APIDescription("""How to call the Jenkins job. ASYNC (the default) means that the job is called in "fire and forget" mode. When set to SYNC, Ontrack will wait for the completion of the job to success, with a given timeout (not recommended).""")
    @APILabel("Call mode")
    val callMode: JenkinsNotificationChannelConfigCallMode = JenkinsNotificationChannelConfigCallMode.ASYNC,
    @APIDescription("Timeout in seconds")
    @APILabel("Timeout")
    val timeout: Int = DEFAULT_TIMEOUT,
) {
    companion object {
        const val DEFAULT_TIMEOUT = 30
    }
}
