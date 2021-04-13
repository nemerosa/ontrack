package net.nemerosa.ontrack.extension.ldap.casc

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.extension.ldap.LDAPSettings
import org.junit.Test
import kotlin.test.assertEquals

class LDAPSettingsContextIT: AbstractCascTestSupport() {

    @Test
    fun `LDAP as CasC`() {
        asAdmin {
            withSettings<LDAPSettings> {
                // Disable the LDAP
                settingsManagerService.saveSettings(
                    LDAPSettings(isEnabled = false)
                )
                // Enables the LDAP through CasC
                casc("""
                    ontrack:
                        config:
                            settings:
                                ldap:
                                    enabled: true
                                    url: "ldaps://ldap.example.com:636/dc=example,dc=com"
                                    user: cn=manager,dc=example,dc=com
                                    password: xxxx
                                    searchBase: ou=people
                                    searchFilter: uid={0}
                                    fullNameAttribute: displayname
                                    groupNameAttribute: cn
                                    groupSearchBase: ou=groups
                                    groupSearchFilter: (uniqueMember={0})
                """.trimIndent())
                // Checks the settings
                val settings = cachedSettingsService.getCachedSettings(LDAPSettings::class.java)
                assertEquals(true, settings.isEnabled)
                assertEquals("ldaps://ldap.example.com:636/dc=example,dc=com", settings.url)
                assertEquals("cn=manager,dc=example,dc=com", settings.user)
                assertEquals("xxxx", settings.password)
                assertEquals("ou=people", settings.searchBase)
                assertEquals("uid={0}", settings.searchFilter)
                assertEquals("displayname", settings.fullNameAttribute)
                assertEquals("", settings.emailAttribute)
                assertEquals("", settings.groupAttribute)
                assertEquals("", settings.groupFilter)
                assertEquals("cn", settings.groupNameAttribute)
                assertEquals("ou=groups", settings.groupSearchBase)
                assertEquals("(uniqueMember={0})", settings.groupSearchFilter)
            }
        }
    }

}