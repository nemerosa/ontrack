package net.nemerosa.ontrack.extension.git.model

import net.nemerosa.ontrack.model.support.UserPassword
import org.junit.Test

import static org.junit.Assert.*

class GitConfigurationTest {

    @Test
    void user_password_none() {
        GitConfiguration configuration = GitConfiguration.empty();
        assertFalse(configuration.getUserPasswordSupplier().get().isPresent());
    }

    @Test
    void user_password_with_user_only() {
        GitConfiguration configuration = GitConfiguration.empty()
                .withUser("test");
        Optional<UserPassword> userPasswordOptional = configuration.getUserPasswordSupplier().get();
        assertTrue(userPasswordOptional.isPresent());
        assertEquals("test", userPasswordOptional.get().getUser());
        assertEquals("", userPasswordOptional.get().getPassword());
    }

    @Test
    void user_password() {
        GitConfiguration configuration = GitConfiguration.empty()
                .withUser("test")
                .withPassword("xxx");
        Optional<UserPassword> userPasswordOptional = configuration.getUserPasswordSupplier().get();
        assertTrue(userPasswordOptional.isPresent());
        assertEquals("test", userPasswordOptional.get().getUser());
        assertEquals("xxx", userPasswordOptional.get().getPassword());
    }

    @Test
    void 'Tag name validation: default'() {
        GitConfiguration configuration = GitConfiguration.empty()
        assert configuration.tagPattern == '*'
        assert configuration.isValidTagName('any')
        assert configuration.isValidTagName('2.0.0')
    }

    @Test
    void 'Tag name validation: simple pattern'() {
        GitConfiguration configuration = GitConfiguration.empty().withTagPattern('2.0.*')
        assert configuration.tagPattern == '2.0.*'
        assert !configuration.isValidTagName('any')
        assert configuration.isValidTagName('2.0.0')
        assert configuration.isValidTagName('2.0.1')
        assert configuration.isValidTagName('2.0.12')
        assert !configuration.isValidTagName('v2.0.12')
        assert !configuration.isValidTagName('2.1.0')
    }

    @Test
    void 'Tag name validation: capturing group'() {
        GitConfiguration configuration = GitConfiguration.empty().withTagPattern('ontrack-(2.0.*)')
        assert configuration.tagPattern == 'ontrack-(2.0.*)'
        assert !configuration.isValidTagName('any')
        assert !configuration.isValidTagName('2.0.0')
        assert configuration.isValidTagName('ontrack-2.0.0')
        assert configuration.isValidTagName('ontrack-2.0.1')
        assert configuration.isValidTagName('ontrack-2.0.12')
        assert !configuration.isValidTagName('v2.0.12')
        assert !configuration.isValidTagName('ontrack-2.1.0')
    }

    @Test
    void 'Build name from tag: default'() {
        GitConfiguration configuration = GitConfiguration.empty()
        assert configuration.getBuildNameFromTagName('any').get() == 'any'
        assert configuration.getBuildNameFromTagName('2.0.0').get() == '2.0.0'
    }

    @Test
    void 'Build name from tag: simple'() {
        GitConfiguration configuration = GitConfiguration.empty().withTagPattern('2.0.*')
        assert !configuration.getBuildNameFromTagName('any').present
        assert configuration.getBuildNameFromTagName('2.0.0').get() == '2.0.0'
        assert configuration.getBuildNameFromTagName('2.0.1').get() == '2.0.1'
        assert configuration.getBuildNameFromTagName('2.0.12').get() == '2.0.12'
        assert !configuration.getBuildNameFromTagName('v2.0.12').present
        assert !configuration.getBuildNameFromTagName('2.1.0').present
    }

    @Test
    void 'Build name from tag: capturing group'() {
        GitConfiguration configuration = GitConfiguration.empty().withTagPattern('ontrack-(2.0.*)')
        assert !configuration.getBuildNameFromTagName('any').present
        assert !configuration.getBuildNameFromTagName('2.0.0').present
        assert configuration.getBuildNameFromTagName('ontrack-2.0.0').get() == '2.0.0'
        assert configuration.getBuildNameFromTagName('ontrack-2.0.1').get() == '2.0.1'
        assert configuration.getBuildNameFromTagName('ontrack-2.0.12').get() == '2.0.12'
        assert !configuration.getBuildNameFromTagName('v2.0.12').present
        assert !configuration.getBuildNameFromTagName('ontrack-2.1.0').present
    }

}