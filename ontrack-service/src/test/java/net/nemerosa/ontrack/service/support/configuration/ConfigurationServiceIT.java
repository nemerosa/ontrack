package net.nemerosa.ontrack.service.support.configuration;

import net.nemerosa.ontrack.extension.support.configurations.EncryptionService;
import net.nemerosa.ontrack.it.AbstractServiceTestSupport;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.support.ConfigurationRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.Assert.*;

public class ConfigurationServiceIT extends AbstractServiceTestSupport {

    public static final String PLAIN_PASSWORD = "verysecret";

    @Autowired
    private TestConfigurationService configurationService;

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Autowired
    private EncryptionService encryptionService;

    @Test
    public void encryption() {
        String crypted = encryptionService.encrypt("test");
        assertNotEquals("test", crypted);
        assertEquals("test", encryptionService.decrypt(crypted));
    }

    @Test
    public void encryptedPasswordForNewConfig() throws Exception {
        // Creates a new configuration, with a password we want to keep secret
        TestConfiguration configuration = new TestConfiguration("test1", "user", PLAIN_PASSWORD);
        asUser().with(GlobalSettings.class).call(() -> {
            // Saves this configuration in the database
            TestConfiguration savedConfiguration = configurationService.newConfiguration(configuration);
            // The returned password is not encrypted (we may need it)
            assertEquals(PLAIN_PASSWORD, savedConfiguration.getPassword());
            // Loads the configuration
            TestConfiguration loadedConfiguration = configurationService.getConfiguration("test1");
            assertEquals(PLAIN_PASSWORD, loadedConfiguration.getPassword());
            // Now, checks the raw result in the repository
            Optional<TestConfiguration> rawConfiguration = configurationRepository.find(TestConfiguration.class, "test1");
            assertTrue(rawConfiguration.isPresent());
            assertNotEquals("Password must be encrypted", PLAIN_PASSWORD, rawConfiguration.get().getPassword());
            // End of test
            return true;
        });
    }

    @Test
    public void encryptedPasswordForSavedConfigWithNewPassword() throws Exception {
        // Creates a new configuration, with a password we want to keep secret
        TestConfiguration configuration = new TestConfiguration("test2", "user", PLAIN_PASSWORD);
        asUser().with(GlobalSettings.class).call(() -> {
            // Saves this configuration in the database
            TestConfiguration savedConfiguration = configurationService.newConfiguration(configuration);
            // Now saves again this configuration with a new password
            configurationService.updateConfiguration(
                    "test2",
                    savedConfiguration.withPassword("newpassword")
            );
            // Loads the configuration
            TestConfiguration loadedConfiguration = configurationService.getConfiguration("test2");
            assertEquals("newpassword", loadedConfiguration.getPassword());
            // Now, checks the raw result in the repository
            Optional<TestConfiguration> rawConfiguration = configurationRepository.find(TestConfiguration.class, "test2");
            assertTrue(rawConfiguration.isPresent());
            assertNotEquals("Password must be encrypted", PLAIN_PASSWORD, rawConfiguration.get().getPassword());
            assertNotEquals("Password must be encrypted", "newpassword", rawConfiguration.get().getPassword());
            // End of test
            return true;
        });
    }

    @Test
    public void encryptedPasswordForSavedConfigWithNoNewPassword() throws Exception {
        // Creates a new configuration, with a password we want to keep secret
        TestConfiguration configuration = new TestConfiguration("test3", "user", PLAIN_PASSWORD);
        asUser().with(GlobalSettings.class).call(() -> {
            // Saves this configuration in the database
            TestConfiguration savedConfiguration = configurationService.newConfiguration(configuration);
            // Now saves again this configuration with no new password
            configurationService.updateConfiguration(
                    "test3",
                    savedConfiguration.withPassword("")
            );
            // Loads the configuration
            TestConfiguration loadedConfiguration = configurationService.getConfiguration("test3");
            assertEquals(PLAIN_PASSWORD, loadedConfiguration.getPassword());
            // Now, checks the raw result in the repository
            Optional<TestConfiguration> rawConfiguration = configurationRepository.find(TestConfiguration.class, "test3");
            assertTrue(rawConfiguration.isPresent());
            assertNotEquals("Password must be encrypted", PLAIN_PASSWORD, rawConfiguration.get().getPassword());
            // End of test
            return true;
        });
    }

    /**
     * Test that the plain configuration that was saved by {@link net.nemerosa.ontrack.service.support.configuration.TestConfigurationUncryptedAction}
     * is encrypted.
     */
    @Test
    public void encryptedConfigurationMigration() throws Exception {
        Optional<TestConfiguration> conf = configurationRepository.find(TestConfiguration.class, "plain");
        assertTrue(conf.isPresent());
        assertNotEquals("Password must have been encrypted by migration", PLAIN_PASSWORD, conf.get().getPassword());
    }

}
