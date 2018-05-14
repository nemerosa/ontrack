package net.nemerosa.ontrack.extension.ldap

import org.junit.Test
import org.springframework.ldap.core.DirContextOperations
import org.springframework.ldap.support.LdapUtils
import org.springframework.security.ldap.userdetails.LdapUserDetails

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class ConfigurableUserDetailsContextMapperTest {

    @Test
    void 'Default attributes filled'() {
        def settings = LDAPSettings.NONE
        ConfigurableUserDetailsContextMapper mapper = new ConfigurableUserDetailsContextMapper(settings)

        def ctx = mock(DirContextOperations)
        when(ctx.getStringAttribute('cn')).thenReturn("User")
        when(ctx.getStringAttribute('email')).thenReturn("user@test.com")
        def originalDetails = mock(LdapUserDetails)

        def details = mapper.extendUserDetails(ctx, originalDetails)
        assert details instanceof ExtendedLDAPUserDetails
        assert details.fullName == 'User'
        assert details.email == 'user@test.com'
    }

    @Test
    void 'Custom attributes filled'() {
        def settings = LDAPSettings.NONE.withFullNameAttribute("fullName").withEmailAttribute("mail")
        ConfigurableUserDetailsContextMapper mapper = new ConfigurableUserDetailsContextMapper(settings)

        def ctx = mock(DirContextOperations)
        when(ctx.getStringAttribute('fullName')).thenReturn("User")
        when(ctx.getStringAttribute('mail')).thenReturn("user@test.com")
        def originalDetails = mock(LdapUserDetails)

        def details = mapper.extendUserDetails(ctx, originalDetails)
        assert details instanceof ExtendedLDAPUserDetails
        assert details.fullName == 'User'
        assert details.email == 'user@test.com'
    }

    @Test
    void 'Collecting the groups'() {
        def settings = LDAPSettings.NONE
        ConfigurableUserDetailsContextMapper mapper = new ConfigurableUserDetailsContextMapper(settings)

        def ctx = mock(DirContextOperations)
        when(ctx.getStringAttribute('cn')).thenReturn("User")
        when(ctx.getStringAttributes('memberOf')).thenReturn(['cn=Admin,ou=Ontrack', 'cn=Developer,ou=PRJ'] as String[])
        def originalDetails = mock(LdapUserDetails)

        def details = mapper.extendUserDetails(ctx, originalDetails)
        assert details instanceof ExtendedLDAPUserDetails
        assert details.fullName == 'User'
        assert details.groups == ['Admin', 'Developer'] as Set
    }

    @Test
    void 'Collecting the groups with custom attribute'() {
        def settings = LDAPSettings.NONE.withGroupAttribute('group')
        ConfigurableUserDetailsContextMapper mapper = new ConfigurableUserDetailsContextMapper(settings)

        def ctx = mock(DirContextOperations)
        when(ctx.getStringAttribute('cn')).thenReturn("User")
        when(ctx.getStringAttributes('group')).thenReturn(['cn=Admin,ou=Ontrack', 'cn=Developer,ou=PRJ'] as String[])
        def originalDetails = mock(LdapUserDetails)

        def details = mapper.extendUserDetails(ctx, originalDetails)
        assert details instanceof ExtendedLDAPUserDetails
        assert details.fullName == 'User'
        assert details.groups == ['Admin', 'Developer'] as Set
    }

    @Test
    void 'Collecting the groups with filter'() {
        def settings = LDAPSettings.NONE.withGroupFilter('Ontrack')
        ConfigurableUserDetailsContextMapper mapper = new ConfigurableUserDetailsContextMapper(settings)

        def ctx = mock(DirContextOperations)
        when(ctx.getStringAttribute('cn')).thenReturn("User")
        when(ctx.getStringAttributes('memberOf')).thenReturn(['cn=Admin,ou=Ontrack', 'cn=Developer,ou=PRJ'] as String[])
        def originalDetails = mock(LdapUserDetails)

        def details = mapper.extendUserDetails(ctx, originalDetails)
        assert details instanceof ExtendedLDAPUserDetails
        assert details.fullName == 'User'
        assert details.groups == ['Admin'] as Set
    }

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
