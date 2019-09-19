package net.nemerosa.ontrack.extension.git.model

import net.nemerosa.ontrack.model.support.UserPassword
import org.junit.Test

import static org.junit.Assert.*

class BasicGitConfigurationTest {

    @Test
    void obfuscation_of_password() {
        BasicGitConfiguration configuration = BasicGitConfiguration.empty()
                .withUser("test").withPassword("secret")
        assertEquals("", configuration.obfuscate().getPassword())
    }

    @Test
    void user_password_none() {
        BasicGitConfiguration configuration = BasicGitConfiguration.empty();
        assertFalse(configuration.credentials.present);
    }

    @Test
    void user_password_with_user_only() {
        BasicGitConfiguration configuration = BasicGitConfiguration.empty()
                .withUser("test")
        Optional<UserPassword> userPasswordOptional = configuration.credentials
        assertTrue(userPasswordOptional.present)
        assertEquals("test", userPasswordOptional.get().user)
        assertEquals("", userPasswordOptional.get().password)
    }

    @Test
    void user_password() {
        BasicGitConfiguration configuration = BasicGitConfiguration.empty()
                .withUser("test")
                .withPassword("xxx");
        Optional<UserPassword> userPasswordOptional = configuration.credentials;
        assertTrue(userPasswordOptional.present);
        assertEquals("test", userPasswordOptional.get().user);
        assertEquals("xxx", userPasswordOptional.get().password);
    }

}