package net.nemerosa.ontrack.extension.jira.tx

import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.extension.jira.JIRAConfiguration
import net.nemerosa.ontrack.extension.jira.client.JIRAClient
import net.nemerosa.ontrack.extension.jira.client.JIRAClientImpl
import net.nemerosa.ontrack.extension.support.client.RestTemplateBasicAuthentication
import net.nemerosa.ontrack.extension.support.client.RestTemplateProvider
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("!${RunProfile.DEV}")
class JIRASessionFactoryImpl(
    private val restTemplateProvider: RestTemplateProvider,
) : JIRASessionFactory {

    override fun create(configuration: JIRAConfiguration): JIRASession {

        // Creates an HTTP client
        val template = restTemplateProvider.createRestTemplate(
            rootUri = configuration.url,
            basicAuthentication = RestTemplateBasicAuthentication(
                username = configuration.user ?: "",
                password = configuration.password ?: "",
            )
        )

        // Creates the client
        val client: JIRAClient = JIRAClientImpl(template)

        // Creates the session
        return object : JIRASession {

            override fun getClient(): JIRAClient {
                return client
            }

            override fun close() {
                client.close()
            }
        }
    }
}
