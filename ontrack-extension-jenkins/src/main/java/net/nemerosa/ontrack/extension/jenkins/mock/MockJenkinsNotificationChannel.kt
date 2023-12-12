package net.nemerosa.ontrack.extension.jenkins.mock

import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.extension.jenkins.JenkinsConfiguration
import net.nemerosa.ontrack.extension.jenkins.JenkinsConfigurationService
import net.nemerosa.ontrack.extension.jenkins.client.JenkinsClient
import net.nemerosa.ontrack.extension.jenkins.notifications.AbstractJenkinsNotificationChannel
import net.nemerosa.ontrack.model.events.EventVariableService
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile(RunProfile.ACC)
class MockJenkinsNotificationChannel(
    jenkinsConfigurationService: JenkinsConfigurationService,
    eventVariableService: EventVariableService,
    private val mockJenkinsClient: MockJenkinsClient,
) : AbstractJenkinsNotificationChannel(
    jenkinsConfigurationService,
    eventVariableService,
) {

    override val type: String = "mock-jenkins"

    override fun createJenkinsClient(config: JenkinsConfiguration): JenkinsClient = mockJenkinsClient

}
