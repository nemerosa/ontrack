package net.nemerosa.ontrack.extension.ldap.casc

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.extension.ldap.LDAPSettings
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.getRequiredTextField
import net.nemerosa.ontrack.json.parseAsJson
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class LDAPSettingsContextIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var ldapSettingsContext: LDAPSettingsContext

    @Test
    fun `CasC schema type`() {
        val type = ldapSettingsContext.jsonType
        assertEquals(
            """
                {
                  "title": "LDAPSettings",
                  "description": null,
                  "properties": {
                    "emailAttribute": {
                      "description": "emailAttribute field",
                      "type": "string"
                    },
                    "fullNameAttribute": {
                      "description": "fullNameAttribute field",
                      "type": "string"
                    },
                    "groupAttribute": {
                      "description": "groupAttribute field",
                      "type": "string"
                    },
                    "groupFilter": {
                      "description": "groupFilter field",
                      "type": "string"
                    },
                    "groupNameAttribute": {
                      "description": "groupNameAttribute field",
                      "type": "string"
                    },
                    "groupSearchBase": {
                      "description": "groupSearchBase field",
                      "type": "string"
                    },
                    "groupSearchFilter": {
                      "description": "groupSearchFilter field",
                      "type": "string"
                    },
                    "enabled": {
                      "description": "Is LDAP authentication enabled?",
                      "type": "boolean"
                    },
                    "password": {
                      "description": "password field",
                      "type": "string"
                    },
                    "searchBase": {
                      "description": "searchBase field",
                      "type": "string"
                    },
                    "searchFilter": {
                      "description": "searchFilter field",
                      "type": "string"
                    },
                    "url": {
                      "description": "url field",
                      "type": "string"
                    },
                    "user": {
                      "description": "user field",
                      "type": "string"
                    }
                  },
                  "required": [
                    "enabled"
                  ],
                  "additionalProperties": false,
                  "type": "object"
                }
            """.trimIndent().parseAsJson(),
            type.asJson()
        )
    }

    @Test
    fun `Obfuscation of the password`() {
        asAdmin {
            withSettings<LDAPSettings> {
                // Disable the LDAP
                settingsManagerService.saveSettings(
                    LDAPSettings(isEnabled = false)
                )
                // Enables the LDAP through CasC
                casc(
                    """
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
                """.trimIndent()
                )
                // Rendering
                val json = ldapSettingsContext.render()
                assertEquals("", json.getRequiredTextField("password"))
            }
        }
    }

    @Test
    fun `LDAP as CasC`() {
        asAdmin {
            withSettings<LDAPSettings> {
                // Disable the LDAP
                settingsManagerService.saveSettings(
                    LDAPSettings(isEnabled = false)
                )
                // Enables the LDAP through CasC
                casc(
                    """
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
                """.trimIndent()
                )
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

    @Test
    fun `LDAP disabled as CasC`() {
        asAdmin {
            withSettings<LDAPSettings> {
                // Disable the LDAP
                settingsManagerService.saveSettings(
                    LDAPSettings(
                        isEnabled = true,
                        url = "ldaps://ldap.example.com:636/dc=example,dc=com",
                        user = "cn=manager,dc=example,dc=com",
                        password = "xxx",
                    )
                )
                // Disabling the LDAP through CasC
                casc(
                    """
                    ontrack:
                        config:
                            settings:
                                ldap:
                                    enabled: false
                """.trimIndent()
                )
                // Checks the settings
                val settings = cachedSettingsService.getCachedSettings(LDAPSettings::class.java)
                assertEquals(false, settings.isEnabled)
            }
        }
    }

}