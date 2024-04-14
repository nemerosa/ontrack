package net.nemerosa.ontrack.extension.jenkins.notifications

data class JenkinsNotificationChannelOutput(
    val jobUrl: String,
    val parameters: List<JenkinsNotificationChannelConfigParam>,
)
