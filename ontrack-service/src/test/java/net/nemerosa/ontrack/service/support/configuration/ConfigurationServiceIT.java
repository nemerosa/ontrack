package net.nemerosa.ontrack.service.support.configuration;

import net.nemerosa.ontrack.it.AbstractServiceTestSupport;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.support.ConfigurationRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.Assert.*;

public class ConfigurationServiceIT extends AbstractServiceTestSupport {

    public static final String PLAIN_PASSWORD = "verysecret";
    public static final String CONFIG_NAME = "test";

    @Autowired
    private TestConfigurationService configurationService;

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Test
    public void encryptedPasswordForNewConfig() throws Exception {
        // Creates a new configuration, with a password we want to keep secret
        TestConfiguration configuration = new TestConfiguration(CONFIG_NAME, "user", PLAIN_PASSWORD);
        asUser().with(GlobalSettings.class).call(() -> {
            // Saves this configuration in the database
            TestConfiguration savedConfiguration = configurationService.newConfiguration(configuration);
            // The returned password is not encrypted (we may need it)
            assertEquals(PLAIN_PASSWORD, savedConfiguration.getPassword());
            // Loads the configuration
            TestConfiguration loadedConfiguration = configurationService.getConfiguration(CONFIG_NAME);
            assertEquals(PLAIN_PASSWORD, loadedConfiguration.getPassword());
            // Now, checks the raw result in the repository
            Optional<TestConfiguration> rawConfiguration = configurationRepository.find(TestConfiguration.class, CONFIG_NAME);
            assertTrue(rawConfiguration.isPresent());
            assertNotEquals("Password must be encrypted", PLAIN_PASSWORD, rawConfiguration.get().getPassword());
            // End of test
            return true;
        });
    }

    // TODO Checks the migration process if possible?
    // TODO Migration of existing configuration through DBInit?

}
