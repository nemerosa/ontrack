package net.nemerosa.ontrack.extension.ldap

import org.junit.Test
import org.springframework.ldap.core.DirContextOperations
import org.springframework.security.ldap.SpringSecurityLdapTemplate
import org.springframework.security.ldap.userdetails.LdapUserDetails

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class ConfigurableUserDetailsContextMapperIT {

    private LDAPSettings settings = LDAPSettings.NONE
    private SpringSecurityLdapTemplate ldapTemplate = mock(SpringSecurityLdapTemplate.class)

    @Test
    void 'Default attributes filled'() {
        ConfigurableUserDetailsContextMapper mapper = new ConfigurableUserDetailsContextMapper(settings, ldapTemplate)

        def ctx = mock(DirContextOperations)
        when(ctx.getStringAttribute('cn')).thenReturn("User")
        when(ctx.getStringAttribute('email')).thenReturn("user@test.com")
        def originalDetails = mock(LdapUserDetails)

        def details = mapper.extendUserDetails(ctx, originalDetails, "user")
        assert details instanceof ExtendedLDAPUserDetails
        assert details.fullName == 'User'
        assert details.email == 'user@test.com'
    }

    @Test
    void 'Custom attributes filled'() {
        settings = settings.withFullNameAttribute("fullName").withEmailAttribute("mail")
        ConfigurableUserDetailsContextMapper mapper = new ConfigurableUserDetailsContextMapper(settings, ldapTemplate)

        def ctx = mock(DirContextOperations)
        when(ctx.getStringAttribute('fullName')).thenReturn("User")
        when(ctx.getStringAttribute('mail')).thenReturn("user@test.com")
        def originalDetails = mock(LdapUserDetails)

        def details = mapper.extendUserDetails(ctx, originalDetails, "user")
        assert details instanceof ExtendedLDAPUserDetails
        assert details.fullName == 'User'
        assert details.email == 'user@test.com'
    }

    @Test
    void 'Collecting the groups'() {
        ConfigurableUserDetailsContextMapper mapper = new ConfigurableUserDetailsContextMapper(settings, ldapTemplate)

        def ctx = mock(DirContextOperations)
        when(ctx.getStringAttribute('cn')).thenReturn("User")
        when(ctx.getStringAttributes('memberOf')).thenReturn(['cn=Admin,ou=Ontrack', 'cn=Developer,ou=PRJ'] as String[])
        def originalDetails = mock(LdapUserDetails)

        def details = mapper.extendUserDetails(ctx, originalDetails, "user")
        assert details instanceof ExtendedLDAPUserDetails
        assert details.fullName == 'User'
        assert details.groups == ['Admin', 'Developer'] as Set
    }

    @Test
    void 'Collecting the groups using reverse link'() {
        settings = settings
                .withGroupSearchBase("ou=groups")
                .withGroupSearchFilter("(uniqueMember={0})")
        ConfigurableUserDetailsContextMapper mapper = new ConfigurableUserDetailsContextMapper(settings, ldapTemplate)

        // No group from the user
        def ctx = mock(DirContextOperations)
        when(ctx.getStringAttribute('cn')).thenReturn("User")
        when(ctx.getStringAttributes('memberOf')).thenReturn([] as String[])
        def originalDetails = mock(LdapUserDetails)

        // Groups from the search
        when(ldapTemplate.searchForSingleAttributeValues(
                "ou=groups",
                "(uniqueMember={0})",
                ["user"] as String[],
                "cn"
        )).thenReturn(['Admin', 'Developer'] as Set<String>)

        def details = mapper.extendUserDetails(ctx, originalDetails, "user")
        assert details instanceof ExtendedLDAPUserDetails
        assert details.fullName == 'User'
        assert details.groups == ['Admin', 'Developer'] as Set
    }

    @Test
    void 'Collecting the groups with custom attribute'() {
        settings = settings.withGroupAttribute('group')
        ConfigurableUserDetailsContextMapper mapper = new ConfigurableUserDetailsContextMapper(settings, ldapTemplate)

        def ctx = mock(DirContextOperations)
        when(ctx.getStringAttribute('cn')).thenReturn("User")
        when(ctx.getStringAttributes('group')).thenReturn(['cn=Admin,ou=Ontrack', 'cn=Developer,ou=PRJ'] as String[])
        def originalDetails = mock(LdapUserDetails)

        def details = mapper.extendUserDetails(ctx, originalDetails, "user")
        assert details instanceof ExtendedLDAPUserDetails
        assert details.fullName == 'User'
        assert details.groups == ['Admin', 'Developer'] as Set
    }

    @Test
    void 'Collecting the groups with filter'() {
        settings = settings.withGroupFilter('Ontrack')
        ConfigurableUserDetailsContextMapper mapper = new ConfigurableUserDetailsContextMapper(settings, ldapTemplate)

        def ctx = mock(DirContextOperations)
        when(ctx.getStringAttribute('cn')).thenReturn("User")
        when(ctx.getStringAttributes('memberOf')).thenReturn(['cn=Admin,ou=Ontrack', 'cn=Developer,ou=PRJ'] as String[])
        def originalDetails = mock(LdapUserDetails)

        def details = mapper.extendUserDetails(ctx, originalDetails, "user")
        assert details instanceof ExtendedLDAPUserDetails
        assert details.fullName == 'User'
        assert details.groups == ['Admin'] as Set
    }

}
