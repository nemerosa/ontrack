package net.nemerosa.ontrack.extension.ldap

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.support.ApplicationLogService
import org.junit.Test
import kotlin.test.assertNull

class LDAPProviderFactoryImplTest {

    @Test
    fun `No provider when LDAP is disabled`() {
        val settingsService = mock<CachedSettingsService>()
        val applicationLogService = mock<ApplicationLogService>()
        whenever(settingsService.getCachedSettings(LDAPSettings::class.java)).thenReturn(LDAPSettings.NONE)
        val factory = LDAPProviderFactoryImpl(settingsService, applicationLogService)
        assertNull(factory.provider)
    }

}