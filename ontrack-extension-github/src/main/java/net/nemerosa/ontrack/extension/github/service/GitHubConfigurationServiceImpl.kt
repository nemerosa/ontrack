package net.nemerosa.ontrack.extension.github.service

import net.nemerosa.ontrack.extension.github.app.GitHubAppTokenService
import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClientFactory
import net.nemerosa.ontrack.extension.github.model.GitHubAuthenticationType
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.extension.github.model.getAppInstallationTokenInformation
import net.nemerosa.ontrack.extension.support.AbstractConfigurationService
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventPostService
import net.nemerosa.ontrack.model.security.EncryptionService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import net.nemerosa.ontrack.model.support.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class GitHubConfigurationServiceImpl(
    configurationRepository: ConfigurationRepository,
    securityService: SecurityService,
    encryptionService: EncryptionService,
    eventPostService: EventPostService,
    eventFactory: EventFactory,
    private val gitHubClientFactory: OntrackGitHubClientFactory,
    ontrackConfigProperties: OntrackConfigProperties,
    private val applicationLogService: ApplicationLogService,
    private val gitHubAppTokenService: GitHubAppTokenService,
) : AbstractConfigurationService<GitHubEngineConfiguration>(
    GitHubEngineConfiguration::class.java,
    configurationRepository,
    securityService,
    encryptionService,
    eventPostService,
    eventFactory,
    ontrackConfigProperties
), GitHubConfigurationService {

    override val type: String = "github"

    override fun checkConfigurationFields(configuration: GitHubEngineConfiguration) {
        configuration.checkFields()
    }

    override fun getConfigExtraData(config: GitHubEngineConfiguration): Any? {
        val client = gitHubClientFactory.create(config)
        val extra = mutableMapOf<String, Any?>()

        // The authentication type is cancelled by the obfuscation
        // so we need to record it differently.
        extra[GitHubEngineConfiguration::authenticationType.name] = config.authenticationType

        extra["rateLimit"] = client.getRateLimit()
        if (config.authenticationType == GitHubAuthenticationType.APP) {
            extra["appToken"] = gitHubAppTokenService.getAppInstallationTokenInformation(config)
        }
        return extra
    }

    override fun validate(configuration: GitHubEngineConfiguration): ConnectionResult =
        try {
            // Gets the client
            val client = gitHubClientFactory.create(configuration)
            // Gets the list of organisations
            client.organizations
            // OK
            ConnectionResult.ok()
        } catch (ex: Exception) {
            applicationLogService.log(
                ApplicationLogEntry.error(
                    ex,
                    nd("github", "GitHub connection issue"),
                    configuration.url
                )
                    .withDetail("github-config-name", configuration.name)
                    .withDetail("github-config-url", configuration.url)
            )
            ConnectionResult.error(ex)
        }

}