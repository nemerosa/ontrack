package net.nemerosa.ontrack.extension.ldap

import org.junit.Test
import org.springframework.ldap.support.LdapUtils
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ConfigurableUserDetailsContextMapperTest {

    @Test
    fun `DN value - lowercase requested from uppercase`() {
        val dn = LdapUtils.newLdapName("cn=Damien,ou=Test")
        assertEquals("Test", ConfigurableUserDetailsContextMapper.getValue(dn, "OU"))
    }

    @Test
    fun `DN value - lowercase requested from lowercase`() {
        val dn = LdapUtils.newLdapName("cn=Damien,ou=Test")
        assertEquals("Test", ConfigurableUserDetailsContextMapper.getValue(dn, "ou"))
    }

    @Test
    fun `DN value - not found`() {
        val dn = LdapUtils.newLdapName("cn=Damien")
        assertNull(ConfigurableUserDetailsContextMapper.getValue(dn, "ou"))
    }

}
