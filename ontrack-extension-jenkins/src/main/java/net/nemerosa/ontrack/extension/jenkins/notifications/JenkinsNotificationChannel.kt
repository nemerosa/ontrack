package net.nemerosa.ontrack.extension.jenkins.notifications

import net.nemerosa.ontrack.extension.jenkins.JenkinsConfiguration
import net.nemerosa.ontrack.extension.jenkins.JenkinsConfigurationService
import net.nemerosa.ontrack.extension.jenkins.client.JenkinsClient
import net.nemerosa.ontrack.extension.jenkins.client.JenkinsClientFactory
import net.nemerosa.ontrack.extension.notifications.channels.NoTemplate
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.Documentation
import net.nemerosa.ontrack.model.docs.DocumentationLink
import net.nemerosa.ontrack.model.events.EventTemplatingService
import org.springframework.stereotype.Component

@APIDescription("This channel is used to trigger remote Jenkins jobs with some parameters.")
@Documentation(JenkinsNotificationChannelConfig::class)
@Documentation(JenkinsNotificationChannelOutput::class, section = "output")
@DocumentationLink(value = "integrations/notifications/jenkins.md", name = "Jenkins notifications")
@NoTemplate
@Component
class JenkinsNotificationChannel(
    jenkinsConfigurationService: JenkinsConfigurationService,
    private val jenkinsClientFactory: JenkinsClientFactory,
    eventTemplatingService: EventTemplatingService,
) : AbstractJenkinsNotificationChannel(
    jenkinsConfigurationService,
    eventTemplatingService,
) {

    override val type: String = "jenkins"

    override val displayName: String = "Jenkins"

    override fun createJenkinsClient(config: JenkinsConfiguration): JenkinsClient =
        jenkinsClientFactory.getClient(config)
}
