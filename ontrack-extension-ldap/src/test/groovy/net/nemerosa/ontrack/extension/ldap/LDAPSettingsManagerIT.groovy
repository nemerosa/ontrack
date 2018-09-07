package net.nemerosa.ontrack.extension.ldap

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.GlobalSettings
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class LDAPSettingsManagerIT extends AbstractServiceTestSupport {

    @Autowired
    private LDAPSettingsManager service

    @Test
    void 'LDAP settings: save and restore'() {
        // Settings to save
        LDAPSettings settings = createSettings()
        LDAPSettings restoredSettings = asUser().with(GlobalSettings).call {
            // Saves the settings
            service.saveSettings(settings)
            // Gets them back
            service.settings
        }
        // Checks they are the same
        assert settings == restoredSettings
    }

    @Test
    void 'LDAP settings: password not saved when blank'() {
        // Settings to save
        LDAPSettings settings = createSettings()
        LDAPSettings restoredSettings = asUser().with(GlobalSettings).call {
            // Saves the settings once
            service.saveSettings(settings)
            // Saves them again, without the password
            service.saveSettings(createSettings(""))
            // Gets the settings back...
            service.settings
        }
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
                "email",
                "",
                "",
                "cn",
                "ou=groups",
                "(uniqueMember={0})"
        )
        settings
    }

}