package net.nemerosa.ontrack.extension.ldap

import org.junit.Test
import org.springframework.ldap.support.LdapUtils

class ConfigurableUserDetailsContextMapperTest {

    @Test
    void 'DN value - lowercase requested from uppercase'() {
        def dn = LdapUtils.newLdapName('cn=Damien,ou=Test')
        assert ConfigurableUserDetailsContextMapper.getValue(dn, 'OU') == 'Test'
    }

    @Test
    void 'DN value - lowercase requested from lowercase'() {
        def dn = LdapUtils.newLdapName('cn=Damien,ou=Test')
        assert ConfigurableUserDetailsContextMapper.getValue(dn, 'ou') == 'Test'
    }

    @Test
    void 'DN value - not found'() {
        def dn = LdapUtils.newLdapName('cn=Damien')
        assert ConfigurableUserDetailsContextMapper.getValue(dn, 'ou') == null
    }

}
