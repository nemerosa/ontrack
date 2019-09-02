package net.nemerosa.ontrack.extension.sonarqube.configuration

import net.nemerosa.ontrack.extension.support.AbstractConfigurationService
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventPostService
import net.nemerosa.ontrack.model.security.EncryptionService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.ConfigurationRepository
import net.nemerosa.ontrack.model.support.ConnectionResult
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.springframework.stereotype.Service

@Service
class SonarQubeConfigurationServiceImpl(
        configurationRepository: ConfigurationRepository,
        securityService: SecurityService,
        encryptionService: EncryptionService,
        eventPostService: EventPostService,
        eventFactory: EventFactory,
        ontrackConfigProperties: OntrackConfigProperties
) : AbstractConfigurationService<SonarQubeConfiguration>(
        SonarQubeConfiguration::class.java,
        configurationRepository,
        securityService,
        encryptionService,
        eventPostService,
        eventFactory,
        ontrackConfigProperties
), SonarQubeConfigurationService {

    override fun validate(configuration: SonarQubeConfiguration): ConnectionResult {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}