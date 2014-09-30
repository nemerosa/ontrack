package net.nemerosa.ontrack.extension.git.model;

import net.nemerosa.ontrack.model.support.UserPassword;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class GitConfigurationTest {

    @Test
    public void user_password_none() {
        GitConfiguration configuration = GitConfiguration.empty();
        assertFalse(configuration.getUserPasswordSupplier().get().isPresent());
    }

    @Test
    public void user_password_with_user_only() {
        GitConfiguration configuration = GitConfiguration.empty()
                .withUser("test");
        Optional<UserPassword> userPasswordOptional = configuration.getUserPasswordSupplier().get();
        assertTrue(userPasswordOptional.isPresent());
        assertEquals("test", userPasswordOptional.get().getUser());
        assertEquals("", userPasswordOptional.get().getPassword());
    }

    @Test
    public void user_password() {
        GitConfiguration configuration = GitConfiguration.empty()
                .withUser("test")
                .withPassword("xxx");
        Optional<UserPassword> userPasswordOptional = configuration.getUserPasswordSupplier().get();
        assertTrue(userPasswordOptional.isPresent());
        assertEquals("test", userPasswordOptional.get().getUser());
        assertEquals("xxx", userPasswordOptional.get().getPassword());
    }

}