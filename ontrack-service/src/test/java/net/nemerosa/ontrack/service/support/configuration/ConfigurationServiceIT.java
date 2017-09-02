package net.nemerosa.ontrack.service.support.configuration;

import net.nemerosa.ontrack.extension.api.support.TestConfiguration;
import net.nemerosa.ontrack.extension.api.support.TestConfigurationService;
import net.nemerosa.ontrack.extension.api.support.TestProperty;
import net.nemerosa.ontrack.extension.api.support.TestPropertyType;
import net.nemerosa.ontrack.it.AbstractServiceTestSupport;
import net.nemerosa.ontrack.model.security.EncryptionService;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.security.ProjectEdit;
import net.nemerosa.ontrack.model.security.ProjectView;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.PropertyService;
import net.nemerosa.ontrack.model.support.ConfigurationRepository;
import net.nemerosa.ontrack.model.support.ConfigurationValidationException;
import net.nemerosa.ontrack.model.support.ConnectionResult;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static net.nemerosa.ontrack.extension.api.support.TestConfiguration.PLAIN_PASSWORD;
import static net.nemerosa.ontrack.extension.api.support.TestConfiguration.config;
import static net.nemerosa.ontrack.test.TestUtils.uid;
import static org.junit.Assert.*;

public class ConfigurationServiceIT extends AbstractServiceTestSupport {

    @Autowired
    private TestConfigurationService configurationService;

    @Autowired
    private ConfigurationRepository configurationRepository;

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private PropertyService propertyService;

    @Test
    public void validate_ok_on_new_configuration() throws Exception {
        TestConfiguration configuration = new TestConfiguration(uid("T"), "check", "test");
        TestConfiguration savedConfig = asUser().with(GlobalSettings.class).call(() ->
                configurationService.newConfiguration(configuration)
        );
        assertEquals(configuration.getName(), savedConfig.getName());
        assertTrue(StringUtils.isEmpty(savedConfig.getPassword()));
    }

    @Test(expected = ConfigurationValidationException.class)
    public void validate_nok_on_new_configuration() throws Exception {
        TestConfiguration configuration = new TestConfiguration(uid("T"), "check", "xxx");
        asUser().with(GlobalSettings.class).call(() ->
                configurationService.newConfiguration(configuration)
        );
    }

    /**
     * Regression test for #531
     */
    @Test
    public void test_ok_on_new_configuration_with_blank_password() throws Exception {
        TestConfiguration configuration = new TestConfiguration(uid("T"), "", "");
        ConnectionResult result = asUser().with(GlobalSettings.class).call(() ->
                configurationService.test(configuration)
        );
        assertEquals(ConnectionResult.ConnectionResultType.OK, result.getType());
    }

    @Test
    public void validate_ok_on_updated_configuration_with_old_password() throws Exception {
        // Creates a configuration
        String name = uid("T");
        TestConfiguration configuration = new TestConfiguration(name, "check", "test");
        asUser().with(GlobalSettings.class).call(() ->
                configurationService.newConfiguration(configuration)
        );
        // Updates the configuration and fills the password
        TestConfiguration updatedConfiguration = new TestConfiguration(name, "check", "test");
        asUser().with(GlobalSettings.class).call(() -> {
            configurationService.updateConfiguration(name, updatedConfiguration);
            return null;
        });
    }

    @Test
    public void validate_ok_on_updated_configuration_without_old_password() throws Exception {
        // Creates a configuration
        String name = uid("T");
        TestConfiguration configuration = new TestConfiguration(name, "check", "test");
        asUser().with(GlobalSettings.class).call(() ->
                configurationService.newConfiguration(configuration)
        );
        // Updates the configuration and fills the password
        TestConfiguration updatedConfiguration = new TestConfiguration(name, "check", "");
        asUser().with(GlobalSettings.class).call(() -> {
            configurationService.updateConfiguration(name, updatedConfiguration);
            return null;
        });
    }

    @Test(expected = ConfigurationValidationException.class)
    public void validate_nok_on_updated_configuration_with_wrong_old_password() throws Exception {
        // Creates a configuration
        String name = uid("T");
        TestConfiguration configuration = new TestConfiguration(name, "check", "test");
        asUser().with(GlobalSettings.class).call(() ->
                configurationService.newConfiguration(configuration)
        );
        // Updates the configuration and fills the password
        TestConfiguration updatedConfiguration = new TestConfiguration(name, "check", "xxx");
        asUser().with(GlobalSettings.class).call(() -> {
            configurationService.updateConfiguration(name, updatedConfiguration);
            return null;
        });
    }

    @Test
    public void encryption() {
        String crypted = encryptionService.encrypt("test");
        assertNotEquals("test", crypted);
        assertEquals("test", encryptionService.decrypt(crypted));
    }

    @Test
    public void encryptedPasswordForNewConfig() throws Exception {
        // Creates a new configuration, with a password we want to keep secret
        TestConfiguration configuration = config("test1");
        asUser().with(GlobalSettings.class).call(() -> {
            // Saves this configuration in the database
            TestConfiguration savedConfiguration = configurationService.newConfiguration(configuration);
            // The returned password is not encrypted (we may need it)
            assertEquals("", savedConfiguration.getPassword());
            // Loads the configuration
            // The returned password is not encrypted (we may need it)
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
        String testName = "test2";
        TestConfiguration configuration = config(testName);
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
        TestConfiguration configuration = config("test3");
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
        assertNotEquals("Password should have been encrypted by migration", PLAIN_PASSWORD, conf.get().getPassword());
    }

    @Test(expected = IllegalArgumentException.class)
    public void update_name_check() throws Exception {
        asUser().with(GlobalSettings.class).call(() -> {
            configurationService.updateConfiguration("test", config("testx"));
            return true;
        });
    }

    /**
     * Checks that the password is unchanged when it is given as blank for the same user.
     */
    @Test
    public void update_blank_password() throws Exception {
        asUser().with(GlobalSettings.class).call(() -> {
            configurationService.newConfiguration(config("test5"));
            // Gets the saved password
            String savedPassword = configurationService.getConfiguration("test5").getPassword();
            // Update with blank password
            configurationService.updateConfiguration("test5", config("test5").withPassword(""));
            // Gets the saved password and compares it
            String newSavedPassword = configurationService.getConfiguration("test5").getPassword();
            assertEquals(savedPassword, newSavedPassword);
            // End of test
            return true;
        });
    }

    /**
     * Checks that the password is changed when it is given as blank for another user.
     */
    @Test
    public void update_blank_password_for_different_user() throws Exception {
        asUser().with(GlobalSettings.class).call(() -> {
            configurationService.newConfiguration(config("test6"));
            // Update with blank password for another user
            configurationService.updateConfiguration("test6", new TestConfiguration("test6", "user2", "").withPassword(""));
            // Gets the configuration back and checks the password is blank
            assertEquals("", configurationService.getConfiguration("test6").getPassword());
            assertEquals("user2", configurationService.getConfiguration("test6").getUser());
            // End of test
            return true;
        });
    }

    /**
     * Checks that configuration properties are removed when an associated configuration is deleted.
     */
    @Test
    public void configuration_property_removed_on_configuration_deleted() throws Exception {
        // Creates two configurations
        String conf1Name = uid("C");
        String conf2Name = uid("C");
        TestConfiguration conf1 = asUser().with(GlobalSettings.class).call(() -> configurationService.newConfiguration(config(conf1Name)));
        TestConfiguration conf2 = asUser().with(GlobalSettings.class).call(() -> configurationService.newConfiguration(config(conf2Name)));
        // Creates two projects
        Project p1 = doCreateProject();
        Project p2 = doCreateProject();
        // Sets the properties
        asUser().with(p1, ProjectEdit.class).call(() ->
                propertyService.editProperty(
                        p1,
                        TestPropertyType.class,
                        TestProperty.of(conf1, "1")
                )
        );
        asUser().with(p2, ProjectEdit.class).call(() ->
                propertyService.editProperty(
                        p2,
                        TestPropertyType.class,
                        TestProperty.of(conf2, "2")
                )
        );
        // Assert the properties are there
        asUser().with(p1, ProjectView.class).execute(() ->
                assertTrue(propertyService.hasProperty(p1, TestPropertyType.class))
        );
        asUser().with(p2, ProjectView.class).execute(() ->
                assertTrue(propertyService.hasProperty(p2, TestPropertyType.class))
        );
        // Deletes the first configuration
        asUser().with(GlobalSettings.class).execute(() ->
                configurationService.deleteConfiguration(conf1Name)
        );
        // Checks the property 1 is gone
        asUser().with(p1, ProjectView.class).execute(() ->
                assertFalse("Project configuration should be gone", propertyService.hasProperty(p1, TestPropertyType.class))
        );
        // ... but not the second one
        asUser().with(p2, ProjectView.class).execute(() ->
                assertTrue(propertyService.hasProperty(p2, TestPropertyType.class))
        );
    }

}
