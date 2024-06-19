package net.nemerosa.ontrack.extension.jenkins.mock

import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.extension.jenkins.JenkinsConfiguration
import net.nemerosa.ontrack.extension.jenkins.client.JenkinsClient
import net.nemerosa.ontrack.extension.jenkins.client.JenkinsClientFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile(RunProfile.ACC)
class MockJenkinsClientFactory : JenkinsClientFactory {

    private val clients = mutableMapOf<String, MockJenkinsClient>()

    fun findClient(config: String) = clients[config]

    override fun getClient(configuration: JenkinsConfiguration): JenkinsClient =
        clients.getOrPut(configuration.name) {
            MockJenkinsClient(configuration)
        }

}
