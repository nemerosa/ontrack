package net.nemerosa.ontrack.extension.jenkins.notifications

import net.nemerosa.ontrack.extension.jenkins.JenkinsConfiguration
import net.nemerosa.ontrack.extension.jenkins.JenkinsConfigurationService
import net.nemerosa.ontrack.extension.jenkins.client.JenkinsClient
import net.nemerosa.ontrack.extension.jenkins.client.JenkinsClientFactory
import net.nemerosa.ontrack.model.events.EventVariableService
import org.springframework.stereotype.Component

@Component
class JenkinsNotificationChannel(
    jenkinsConfigurationService: JenkinsConfigurationService,
    private val jenkinsClientFactory: JenkinsClientFactory,
    eventVariableService: EventVariableService,
) : AbstractJenkinsNotificationChannel(
    jenkinsConfigurationService,
    eventVariableService,
) {

    override val type: String = "jenkins"

    override fun createJenkinsClient(config: JenkinsConfiguration): JenkinsClient =
        jenkinsClientFactory.getClient(config)
}
