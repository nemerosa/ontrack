package net.nemerosa.ontrack.service.settings

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.settings.LDAPSettings
import net.nemerosa.ontrack.service.support.SettingsInternalService
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class SettingsInternalServiceImplIT extends AbstractServiceTestSupport {

    @Autowired
    private SettingsInternalService service

    @Test
    void 'LDAP settings: save and restore'() {
        // Settings to save
        LDAPSettings settings = createSettings()
        // Saves the settings
        service.saveLDAPSettings(settings)
        // Gets them back
        LDAPSettings restoredSettings = service.getLDAPSettings()
        // Checks they are the same
        assert settings == restoredSettings
    }

    @Test
    void 'LDAP settings: password not saved when blank'() {
        // Settings to save
        LDAPSettings settings = createSettings()
        // Saves the settings once
        service.saveLDAPSettings(settings)
        // Saves them again, without the password
        service.saveLDAPSettings(createSettings(""))
        // Gets the settings back...
        LDAPSettings restoredSettings = service.getLDAPSettings()
        // Checks they are the same
        assert settings == restoredSettings
    }

    protected static LDAPSettings createSettings() {
        createSettings("verysecret")
    }

    protected static LDAPSettings createSettings(String password) {
        LDAPSettings settings = new LDAPSettings(
                true,
                "ldap://server",
                "searchBase",
                "searchFilter",
                "user",
                password,
                "fullName",
                "email"
        )
        settings
    }

}