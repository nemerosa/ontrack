package net.nemerosa.ontrack.extension.api.support

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
class TestConfigurationServiceImpl(
    configurationRepository: ConfigurationRepository,
    securityService: SecurityService,
    encryptionService: EncryptionService,
    eventPostService: EventPostService,
    eventFactory: EventFactory,
    ontrackConfigProperties: OntrackConfigProperties
) : AbstractConfigurationService<TestConfiguration>(
    TestConfiguration::class.java,
    configurationRepository,
    securityService,
    encryptionService,
    eventPostService,
    eventFactory,
    ontrackConfigProperties
), TestConfigurationService {

    override val type: String get() = "test"

    override fun validate(configuration: TestConfiguration): ConnectionResult {
        return if ("check" == configuration.user) {
            if ("test" == configuration.password) {
                ConnectionResult.ok()
            } else {
                ConnectionResult.error("Wrong password")
            }
        } else {
            ConnectionResult.ok()
        }
    }
}
