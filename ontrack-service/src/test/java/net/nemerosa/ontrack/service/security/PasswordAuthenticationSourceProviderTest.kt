package net.nemerosa.ontrack.service.security

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.model.security.AuthenticationSourceProvider
import net.nemerosa.ontrack.model.security.BuiltinAuthenticationSourceProvider
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.SecuritySettings
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PasswordAuthenticationSourceProviderTest {

    @Test
    fun `Password change is allowed`() {
        val cachedSettingsService = mockk<CachedSettingsService>()
        every { cachedSettingsService.getCachedSettings(SecuritySettings::class.java) } returns SecuritySettings(
            isGrantProjectViewToAll = false,
            isGrantProjectParticipationToAll = false,
        )
        val provider: AuthenticationSourceProvider = BuiltinAuthenticationSourceProvider(
            cachedSettingsService
        )
        val source = provider.sources.firstOrNull()
        assertNotNull(source) {
            assertTrue(it.isAllowingPasswordChange)
        }
    }

    @Test
    fun `Built-in authentication is enabled by default`() {
        val cachedSettingsService = mockk<CachedSettingsService>()
        every { cachedSettingsService.getCachedSettings(SecuritySettings::class.java) } returns SecuritySettings(
            isGrantProjectViewToAll = false,
            isGrantProjectParticipationToAll = false,
        )
        val provider: AuthenticationSourceProvider = BuiltinAuthenticationSourceProvider(
            cachedSettingsService
        )
        val source = provider.sources.firstOrNull()
        assertNotNull(source) {
            assertTrue(it.isEnabled, "Built-in authentication is enabled by default")
        }
    }

    @Test
    fun `Built-in authentication can be disabled`() {
        val cachedSettingsService = mockk<CachedSettingsService>()
        every { cachedSettingsService.getCachedSettings(SecuritySettings::class.java) } returns SecuritySettings(
            isGrantProjectViewToAll = false,
            isGrantProjectParticipationToAll = false,
            builtInAuthenticationEnabled = false,
        )
        val provider: AuthenticationSourceProvider = BuiltinAuthenticationSourceProvider(
            cachedSettingsService
        )
        val source = provider.sources.firstOrNull()
        assertNotNull(source) {
            assertFalse(it.isEnabled, "Built-in authentication is disabled")
        }
    }

}