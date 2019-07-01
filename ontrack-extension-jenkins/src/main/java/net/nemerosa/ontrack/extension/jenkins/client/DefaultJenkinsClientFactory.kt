package net.nemerosa.ontrack.extension.jenkins.client

import net.nemerosa.ontrack.extension.jenkins.JenkinsConfiguration
import net.nemerosa.ontrack.extension.jenkins.JenkinsConfigurationProperties
import net.nemerosa.ontrack.extension.support.client.ClientConnection
import net.nemerosa.ontrack.extension.support.client.ClientFactory
import org.springframework.stereotype.Component

@Component
class DefaultJenkinsClientFactory(
        private val clientFactory: ClientFactory,
        private val jenkinsConfigurationProperties: JenkinsConfigurationProperties
) : JenkinsClientFactory {

    override fun getClient(configuration: JenkinsConfiguration): JenkinsClient {
        return DefaultJenkinsClient(
                clientFactory.getJsonClient(
                        ClientConnection(
                                configuration.url,
                                configuration.user,
                                configuration.password,
                                jenkinsConfigurationProperties.timeout
                        )
                )
        )
    }

}
