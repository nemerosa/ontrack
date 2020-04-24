package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.BuiltinAuthenticationSourceProvider
import org.junit.Test

class PasswordAuthenticationSourceProviderTest {

    @Test
    void 'Password change is allowed'() {
        assert new BuiltinAuthenticationSourceProvider().source.allowingPasswordChange
    }

}