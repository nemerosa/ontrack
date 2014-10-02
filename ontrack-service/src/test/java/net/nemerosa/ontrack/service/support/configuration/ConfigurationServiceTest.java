package net.nemerosa.ontrack.service.support.configuration;

import net.nemerosa.ontrack.extension.support.configurations.ConfigurationNotFoundException;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.support.ConfigurationRepository;
import net.nemerosa.ontrack.security.EncryptionService;
import org.junit.Before;
import org.junit.Test;

import java.util.function.Function;

import static net.nemerosa.ontrack.service.support.configuration.TestConfiguration.config;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConfigurationServiceTest {

    private TestConfigurationService configurationService;
    private SecurityService securityService;

    @Before
    public void before() {
        ConfigurationRepository configurationRepository = mock(ConfigurationRepository.class);
        securityService = mock(SecurityService.class);
        EncryptionService encryptionService = mock(EncryptionService.class);
        configurationService = new TestConfigurationServiceImpl(
                configurationRepository,
                securityService,
                encryptionService
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
        assertEquals(config("new"), replacedConfig);
    }

}
