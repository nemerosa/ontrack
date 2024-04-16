package net.nemerosa.ontrack.extension.jira.tx

import net.nemerosa.ontrack.extension.jira.JIRAConfiguration
import net.nemerosa.ontrack.extension.jira.JIRAConfigurationProperties
import net.nemerosa.ontrack.extension.jira.client.JIRAClient
import net.nemerosa.ontrack.extension.jira.client.JIRAClientImpl
import net.nemerosa.ontrack.extension.support.client.RestTemplateBasicAuthentication
import net.nemerosa.ontrack.extension.support.client.RestTemplateProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    prefix = JIRAConfigurationProperties.JIRA_MOCK_PREFIX,
    name = [JIRAConfigurationProperties.JIRA_MOCK_ENABLED],
    havingValue = "false",
    matchIfMissing = true
)
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
