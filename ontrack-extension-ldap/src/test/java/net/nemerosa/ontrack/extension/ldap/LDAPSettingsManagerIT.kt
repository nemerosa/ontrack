package net.nemerosa.ontrack.extension.ldap

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.security.GlobalSettings
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class LDAPSettingsManagerIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var service: LDAPSettingsManager

    @Test
    fun `LDAP settings save and restore`() {
        // Settings to save
        val settings = createSettings()
        val restoredSettings = asUserWith<GlobalSettings, LDAPSettings> {
            // Saves the settings
            service.saveSettings(settings)
            // Gets them back
            service.settings
        }
        // Checks they are the same
        assertEquals(restoredSettings, settings)
    }

    @Test
    fun `Minimal LDAP settings save and restore`() {
        // Settings to save
        val settings = LDAPSettings(
                true,
                "ldap://server",
                "searchBase",
                "searchFilter",
                "user",
                "verysecret"
        )
        val restoredSettings = asUserWith<GlobalSettings, LDAPSettings> {
            // Saves the settings
            service.saveSettings(settings)
            // Gets them back
            service.settings
        }
        // Checks they are the same
        assertEquals(restoredSettings, settings)
    }

    @Test
    fun `LDAP settings password not saved when blank`() {
        // Settings to save
        val settings = createSettings()
        val restoredSettings = asUserWith<GlobalSettings, LDAPSettings> {
            // Saves the settings once
            service.saveSettings(settings)
            // Saves them again, without the password
            service.saveSettings(createSettings(""))
            // Gets the settings back...
            service.settings
        }
        // Checks they are the same
        assertEquals(restoredSettings, settings)
    }

    companion object {

        protected fun createSettings(): LDAPSettings = createSettings("verysecret")

        protected fun createSettings(password: String) =
                LDAPSettings(
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

    }

}