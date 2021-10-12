package net.nemerosa.ontrack.extension.github.service

import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.ConfigurationRepository
import net.nemerosa.ontrack.model.support.StartupService
import org.springframework.stereotype.Component

/**
 * This code migrates the plain tokens to encrypted ones.
 */
@Component
class GitHubConfigurationTokenMigration(
    private val securityService: SecurityService,
    private val configurationRepository: ConfigurationRepository,
): StartupService {

    override fun getName(): String = "Encryption of GitHub OAuth2 tokens"

    override fun startupOrder(): Int = StartupService.JOB_REGISTRATION

    override fun start() {
        securityService.asAdmin {
            // Gets all existing configurations in a raw form
        }
    }
}