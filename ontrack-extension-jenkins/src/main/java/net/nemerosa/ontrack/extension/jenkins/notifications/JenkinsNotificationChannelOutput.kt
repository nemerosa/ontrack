package net.nemerosa.ontrack.extension.jenkins.notifications

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.DocumentationList

data class JenkinsNotificationChannelOutput(
    @APIDescription("URL to the job")
    val jobUrl: String,
    @APIDescription("URL to the build (only available when call mode is SYNC)")
    val buildUrl: String?,
    @APIDescription("Parameters sent to the job")
    @DocumentationList
    val parameters: List<JenkinsNotificationChannelConfigParam>,
) {
    fun withBuildUrl(buildUrl: String?) = JenkinsNotificationChannelOutput(
        jobUrl = jobUrl,
        buildUrl = buildUrl,
        parameters = parameters
    )
}
