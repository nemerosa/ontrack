package net.nemerosa.ontrack.extension.ldap

import net.nemerosa.ontrack.model.settings.CachedSettingsService
import org.junit.Test

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class LDAPProviderFactoryImplTest {

    @Test
    void 'No provider when LDAP is disabled'() {
        CachedSettingsService settingsService = mock(CachedSettingsService)
        when(settingsService.getCachedSettings(LDAPSettings)).thenReturn(LDAPSettings.NONE)
        LDAPProviderFactoryImpl factory = new LDAPProviderFactoryImpl(settingsService)
        assert factory.provider == null
    }

}