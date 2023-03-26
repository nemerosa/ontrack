package net.nemerosa.ontrack.extension.tfc.config

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
class TFCConfigurationServiceImpl(
    configurationRepository: ConfigurationRepository,
    securityService: SecurityService,
    encryptionService: EncryptionService,
    eventPostService: EventPostService,
    eventFactory: EventFactory,
    ontrackConfigProperties: OntrackConfigProperties,
) : AbstractConfigurationService<TFCConfiguration>(
    TFCConfiguration::class.java,
    configurationRepository,
    securityService,
    encryptionService,
    eventPostService,
    eventFactory,
    ontrackConfigProperties
), TFCConfigurationService {

    override fun validate(configuration: TFCConfiguration): ConnectionResult {
        TODO()
    }

}