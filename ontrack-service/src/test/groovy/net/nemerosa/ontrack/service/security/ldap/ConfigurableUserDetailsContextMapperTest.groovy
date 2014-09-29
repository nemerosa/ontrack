package net.nemerosa.ontrack.service.security.ldap

import net.nemerosa.ontrack.model.settings.LDAPSettings
import org.junit.Test
import org.mockito.Mockito
import org.springframework.ldap.core.DirContextOperations
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

        def details = mapper.extendUserDetails(ctx, 'user', originalDetails)
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

        def details = mapper.extendUserDetails(ctx, 'user', originalDetails)
        assert details instanceof ExtendedLDAPUserDetails
        assert details.fullName == 'User'
        assert details.email == 'user@test.com'
    }

}
