package net.nemerosa.ontrack.extension.git.service

import net.nemerosa.ontrack.extension.git.casc.GitConfigService
import net.nemerosa.ontrack.extension.git.model.BasicGitConfiguration
import net.nemerosa.ontrack.extension.support.AbstractConfigurationService
import net.nemerosa.ontrack.git.GitRepositoryClientFactory
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventPostService
import net.nemerosa.ontrack.model.security.EncryptionService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.ConfigurationRepository
import net.nemerosa.ontrack.model.support.ConnectionResult
import net.nemerosa.ontrack.model.support.ConnectionResult.Companion.ok
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class GitConfigurationServiceImpl @Autowired constructor(
    configurationRepository: ConfigurationRepository,
    securityService: SecurityService,
    encryptionService: EncryptionService,
    eventPostService: EventPostService,
    eventFactory: EventFactory,
    private val repositoryClientFactory: GitRepositoryClientFactory,
    private val gitConfigService: GitConfigService,
    ontrackConfigProperties: OntrackConfigProperties
) : AbstractConfigurationService<BasicGitConfiguration>(
    BasicGitConfiguration::class.java,
    configurationRepository,
    securityService,
    encryptionService,
    eventPostService,
    eventFactory,
    ontrackConfigProperties
), GitConfigurationService {

    override val type: String = "git"

    override fun validate(configuration: BasicGitConfiguration): ConnectionResult {
        try {
            repositoryClientFactory.getClient(configuration.gitRepository, gitConfigService.gitConnectionConfig).test()
            return ok()
        } catch (ex: Exception) {
            return ConnectionResult.error(ex.message!!)
        }
    }
}
