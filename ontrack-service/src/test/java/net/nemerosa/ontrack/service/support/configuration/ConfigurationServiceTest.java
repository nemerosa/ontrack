package net.nemerosa.ontrack.service.support.configuration;

import net.nemerosa.ontrack.extension.api.support.TestConfiguration;
import net.nemerosa.ontrack.extension.api.support.TestConfigurationService;
import net.nemerosa.ontrack.extension.api.support.TestConfigurationServiceImpl;
import net.nemerosa.ontrack.model.events.Event;
import net.nemerosa.ontrack.model.events.EventFactory;
import net.nemerosa.ontrack.model.events.EventPostService;
import net.nemerosa.ontrack.model.security.EncryptionService;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.ConfigurationRepository;
import net.nemerosa.ontrack.model.support.OntrackConfigProperties;
import org.junit.Before;
import org.junit.Test;

import static net.nemerosa.ontrack.extension.api.support.TestConfiguration.PLAIN_PASSWORD;
import static net.nemerosa.ontrack.extension.api.support.TestConfiguration.config;
import static org.mockito.Mockito.*;

public class ConfigurationServiceTest {

    private TestConfigurationService configurationService;
    private EventPostService eventPostService;
    private EventFactory eventFactory;
    private ConfigurationRepository configurationRepository;
    private EncryptionService encryptionService;

    @Before
    public void before() {
        configurationRepository = mock(ConfigurationRepository.class);
        SecurityService securityService = mock(SecurityService.class);
        encryptionService = mock(EncryptionService.class);
        eventPostService = mock(EventPostService.class);
        eventFactory = mock(EventFactory.class);
        OntrackConfigProperties ontrackConfigProperties = new OntrackConfigProperties();
        configurationService = new TestConfigurationServiceImpl(
                configurationRepository,
                securityService,
                encryptionService,
                eventPostService,
                eventFactory,
                ontrackConfigProperties
        );
    }

    @Test
    public void event_on_new_configuration() {
        TestConfiguration config = config("test");
        Event event = Event.of(EventFactory.NEW_CONFIGURATION).with("configuration", "test").get();
        when(eventFactory.newConfiguration(config)).thenReturn(event);
        configurationService.newConfiguration(config);
        verify(eventPostService).post(event);
    }

    @Test
    public void event_on_update_configuration() {
        TestConfiguration config = config("test");
        Event event = Event.of(EventFactory.UPDATE_CONFIGURATION).with("configuration", "test").get();
        when(eventFactory.updateConfiguration(config)).thenReturn(event);
        when(configurationRepository.find(TestConfiguration.class, "test")).thenReturn(config);
        configurationService.updateConfiguration("test", config);
        verify(eventPostService).post(event);
    }

    @Test
    public void event_on_delete_configuration() {
        TestConfiguration config = config("test");
        Event event = Event.of(EventFactory.DELETE_CONFIGURATION).with("configuration", "test").get();
        when(configurationRepository.find(TestConfiguration.class, "test")).thenReturn(config);
        when(eventFactory.deleteConfiguration(config.withPassword("xxxxx"))).thenReturn(event);
        when(encryptionService.decrypt(PLAIN_PASSWORD)).thenReturn("xxxxx");
        configurationService.deleteConfiguration("test");
        verify(eventPostService).post(event);
    }

}
