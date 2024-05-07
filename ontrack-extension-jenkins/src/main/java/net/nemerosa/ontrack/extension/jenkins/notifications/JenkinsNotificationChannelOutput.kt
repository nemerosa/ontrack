package net.nemerosa.ontrack.extension.jenkins.notifications

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.DocumentationList

data class JenkinsNotificationChannelOutput(
    @APIDescription("URL to the job")
    val jobUrl: String,
    @APIDescription("Parameters sent to the job")
    @DocumentationList
    val parameters: List<JenkinsNotificationChannelConfigParam>,
)
