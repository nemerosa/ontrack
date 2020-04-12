package net.nemerosa.ontrack.service.security

import org.junit.Test

class PasswordAuthenticationSourceProviderTest {

    @Test
    void 'Password change is allowed'() {
        assert new BuiltinAuthenticationSourceProvider().source.allowingPasswordChange
    }

}