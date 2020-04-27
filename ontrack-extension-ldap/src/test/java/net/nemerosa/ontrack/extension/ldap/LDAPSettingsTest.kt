package net.nemerosa.ontrack.extension.ldap

import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.json.parseAsJson
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LDAPSettingsTest {

    @Test
    fun `Parsing of JSON`() {
        val json = """{"enabled":true,"url":"ldaps://ldap.company.com:636","searchBase":"dc=company,dc=com","searchFilter":"(sAMAccountName={0})","user":"service","password":"secret","fullNameAttribute":null,"emailAttribute":null,"groupAttribute":null,"groupFilter":null,"groupNameAttribute":null,"groupSearchBase":null,"groupSearchFilter":null}"""
        val node = json.parseAsJson()
        val settings = node.parse<LDAPSettings>()
        settings.apply {
            assertEquals(true, isEnabled)
            assertEquals("ldaps://ldap.company.com:636", url)
            assertEquals("dc=company,dc=com", searchBase)
            assertEquals("(sAMAccountName={0})", searchFilter)
            assertEquals("service", user)
            assertEquals("secret", password)
            assertNull(fullNameAttribute)
            assertNull(emailAttribute)
            assertNull(groupAttribute)
            assertNull(groupFilter)
            assertNull(groupNameAttribute)
            assertNull(groupSearchBase)
            assertNull(groupSearchFilter)
        }
    }

    @Test
    fun `Parsing of default JSON`() {
        val json = """{"enabled":false,"url":"","searchBase":"","searchFilter":"","user":"","password":"","fullNameAttribute":null,"emailAttribute":null,"groupAttribute":null,"groupFilter":null,"groupNameAttribute":null,"groupSearchBase":null,"groupSearchFilter":null}"""
        val node = json.parseAsJson()
        val settings = node.parse<LDAPSettings>()
        settings.apply {
            assertEquals(false, isEnabled)
            assertEquals("", url)
            assertEquals("", searchBase)
            assertEquals("", searchFilter)
            assertEquals("", user)
            assertEquals("", password)
            assertNull(fullNameAttribute)
            assertNull(emailAttribute)
            assertNull(groupAttribute)
            assertNull(groupFilter)
            assertNull(groupNameAttribute)
            assertNull(groupSearchBase)
            assertNull(groupSearchFilter)
        }
    }

}