package net.nemerosa.ontrack.extension.git.model

import net.nemerosa.ontrack.model.support.UserPassword
import org.junit.Test

import static org.junit.Assert.*

class GitConfigurationTest {

    @Test
    void user_password_none() {
        FormerGitConfiguration configuration = FormerGitConfiguration.empty();
        assertFalse(configuration.getUserPasswordSupplier().get().isPresent());
    }

    @Test
    void user_password_with_user_only() {
        FormerGitConfiguration configuration = FormerGitConfiguration.empty()
                .withUser("test");
        Optional<UserPassword> userPasswordOptional = configuration.getUserPasswordSupplier().get();
        assertTrue(userPasswordOptional.isPresent());
        assertEquals("test", userPasswordOptional.get().getUser());
        assertEquals("", userPasswordOptional.get().getPassword());
    }

    @Test
    void user_password() {
        FormerGitConfiguration configuration = FormerGitConfiguration.empty()
                .withUser("test")
                .withPassword("xxx");
        Optional<UserPassword> userPasswordOptional = configuration.getUserPasswordSupplier().get();
        assertTrue(userPasswordOptional.isPresent());
        assertEquals("test", userPasswordOptional.get().getUser());
        assertEquals("xxx", userPasswordOptional.get().getPassword());
    }

}