package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.BuiltinAuthenticationSourceProvider
import org.junit.Test
import kotlin.test.assertTrue

class PasswordAuthenticationSourceProviderTest {

    @Test
    fun `Password change is allowed`() {
        assertTrue(BuiltinAuthenticationSourceProvider.SOURCE.isAllowingPasswordChange)
    }

}