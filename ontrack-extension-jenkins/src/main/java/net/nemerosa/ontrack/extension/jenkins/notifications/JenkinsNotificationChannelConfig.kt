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
    @APIDescription("How to call the Jenkins job.")
    @APILabel("Call mode")
    val callMode: JenkinsNotificationChannelConfigCallMode,
    @APIDescription("Timeout in seconds")
    @APILabel("Timeout")
    val timeout: Int,
)
