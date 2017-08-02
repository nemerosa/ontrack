package net.nemerosa.ontrack.service.support.configuration;

import net.nemerosa.ontrack.extension.api.support.TestConfiguration;
import net.nemerosa.ontrack.extension.api.support.TestConfigurationService;
import net.nemerosa.ontrack.extension.api.support.TestConfigurationServiceImpl;
import net.nemerosa.ontrack.model.events.Event;
import net.nemerosa.ontrack.model.events.EventFactory;
import net.nemerosa.ontrack.model.events.EventPostService;
import net.nemerosa.ontrack.model.security.EncryptionService;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.ConfigurationNotFoundException;
import net.nemerosa.ontrack.model.support.ConfigurationRepository;
import net.nemerosa.ontrack.model.support.OntrackConfigProperties;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.function.Function;

import static net.nemerosa.ontrack.extension.api.support.TestConfiguration.PLAIN_PASSWORD;
import static net.nemerosa.ontrack.extension.api.support.TestConfiguration.config;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

public class ConfigurationServiceTest {

    private TestConfigurationService configurationService;
    private SecurityService securityService;
    private EventPostService eventPostService;
    private EventFactory eventFactory;
    private ConfigurationRepository configurationRepository;
    private EncryptionService encryptionService;

    @Before
    public void before() {
        configurationRepository = mock(ConfigurationRepository.class);
        securityService = mock(SecurityService.class);
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
    public void replace_configuration_same() {
        TestConfiguration config = config("test");
        TestConfiguration replacedConfig = configurationService.replaceConfiguration(
                config,
                Function.identity()
        );
        assertSame("Same config is returned", config, replacedConfig);
    }

    @Test(expected = ConfigurationNotFoundException.class)
    public void replace_configuration_different_not_authorised() {
        TestConfiguration config = config("test");
        when(securityService.isGlobalFunctionGranted(GlobalSettings.class)).thenReturn(false);
        configurationService.replaceConfiguration(
                config,
                s -> s.replaceAll("test", "new")
        );
    }

    @Test
    public void replace_configuration_different_authorised() {
        TestConfiguration config = config("test");
        when(securityService.isGlobalFunctionGranted(GlobalSettings.class)).thenReturn(true);
        TestConfiguration replacedConfig = configurationService.replaceConfiguration(
                config,
                s -> s.replaceAll("test", "new")
        );
        assertEquals(config("new").withPassword(""), replacedConfig);
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
        when(configurationRepository.find(TestConfiguration.class, "test")).thenReturn(Optional.of(config));
        configurationService.updateConfiguration("test", config);
        verify(eventPostService).post(event);
    }

    @Test
    public void event_on_delete_configuration() {
        TestConfiguration config = config("test");
        Event event = Event.of(EventFactory.DELETE_CONFIGURATION).with("configuration", "test").get();
        when(configurationRepository.find(TestConfiguration.class, "test")).thenReturn(Optional.of(config));
        when(eventFactory.deleteConfiguration(config.withPassword("xxxxx"))).thenReturn(event);
        when(encryptionService.decrypt(PLAIN_PASSWORD)).thenReturn("xxxxx");
        configurationService.deleteConfiguration("test");
        verify(eventPostService).post(event);
    }

}
