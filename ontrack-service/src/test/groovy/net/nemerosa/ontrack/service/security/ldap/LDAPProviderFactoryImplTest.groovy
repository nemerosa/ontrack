package net.nemerosa.ontrack.service.security.ldap

import net.nemerosa.ontrack.model.settings.LDAPSettings
import net.nemerosa.ontrack.service.support.SettingsInternalService
import org.junit.Test

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class LDAPProviderFactoryImplTest {

    @Test
    void 'No provider when LDAP is disabled'() {
        SettingsInternalService settingsService = mock(SettingsInternalService)
        when(settingsService.getLDAPSettings()).thenReturn(LDAPSettings.NONE)
        LDAPProviderFactoryImpl factory = new LDAPProviderFactoryImpl(settingsService)
        assert factory.provider == null
    }

}