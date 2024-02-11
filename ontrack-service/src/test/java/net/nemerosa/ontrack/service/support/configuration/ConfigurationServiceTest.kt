package net.nemerosa.ontrack.service.support.configuration

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import net.nemerosa.ontrack.extension.api.support.TestConfiguration
import net.nemerosa.ontrack.extension.api.support.TestConfiguration.Companion.config
import net.nemerosa.ontrack.extension.api.support.TestConfigurationService
import net.nemerosa.ontrack.extension.api.support.TestConfigurationServiceImpl
import net.nemerosa.ontrack.it.MockSecurityService
import net.nemerosa.ontrack.model.events.Event.Companion.of
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventPostService
import net.nemerosa.ontrack.model.security.EncryptionService
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.ConfigurationRepository
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ConfigurationServiceTest {

    private lateinit var configurationService: TestConfigurationService
    private lateinit var eventPostService: EventPostService
    private lateinit var eventFactory: EventFactory
    private lateinit var configurationRepository: ConfigurationRepository
    private lateinit var encryptionService: EncryptionService

    @BeforeEach
    fun before() {
        configurationRepository = mockk<ConfigurationRepository>(relaxed = true)

        every {
            configurationRepository.find(
                TestConfiguration::class.java,
                any()
            )
        } returns null

        val securityService = MockSecurityService()
        encryptionService = mockk<EncryptionService>(relaxed = true)
        eventPostService = mockk<EventPostService>(relaxed = true)
        eventFactory = mockk<EventFactory>()
        val ontrackConfigProperties = OntrackConfigProperties()
        configurationService = TestConfigurationServiceImpl(
            configurationRepository,
            securityService,
            encryptionService,
            eventPostService,
            eventFactory,
            ontrackConfigProperties
        )
    }

    @Test
    fun event_on_new_configuration() {
        val name = uid("cfg-")
        val config = config(name)
        val event = of(EventFactory.NEW_CONFIGURATION).with("CONFIGURATION", name).build()

        every { eventFactory.newConfiguration(config) } returns event
        configurationService.newConfiguration(config)
        verify {
            eventPostService.post(event)
        }
    }

    @Test
    fun event_on_update_configuration() {
        val config = config("test")
        val event = of(EventFactory.UPDATE_CONFIGURATION).with("CONFIGURATION", "test").build()

        every {
            eventFactory.updateConfiguration(config)
        } returns event

        every {
            configurationRepository.find(
                TestConfiguration::class.java, "test"
            )
        } returns config

        configurationService.updateConfiguration("test", config)
        verify {
            eventPostService.post(event)
        }
    }

    @Test
    fun event_on_delete_configuration() {
        val config = config("test")
        val event = of(EventFactory.DELETE_CONFIGURATION).with("CONFIGURATION", "test").build()
        every {
            configurationRepository.find(
                TestConfiguration::class.java, "test"
            )
        } returns config

        every {
            eventFactory.deleteConfiguration(config.withPassword("xxxxx"))
        } returns event

        every {
            encryptionService.decrypt(TestConfiguration.PLAIN_PASSWORD)
        } returns "xxxxx"

        configurationService.deleteConfiguration("test")

        verify {
            eventPostService.post(event)
        }
    }
}
