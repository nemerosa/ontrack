package net.nemerosa.ontrack.extension.ldap

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import net.nemerosa.ontrack.test.assertIs
import org.junit.Before
import org.junit.Test
import org.springframework.ldap.core.DirContextOperations
import org.springframework.security.ldap.SpringSecurityLdapTemplate
import org.springframework.security.ldap.userdetails.LdapUserDetails
import kotlin.test.assertEquals

class ConfigurableUserDetailsContextMapperIT {

    private lateinit var settings: LDAPSettings
    private val ldapTemplate = mock<SpringSecurityLdapTemplate>()

    @Before
    fun before() {
        settings = LDAPSettings.NONE
    }

    @Test
    fun `Default attributes filled`() {
        val mapper = ConfigurableUserDetailsContextMapper(settings, ldapTemplate)

        val ctx = mock<DirContextOperations>()
        whenever(ctx.getStringAttribute("cn")).thenReturn("User")
        whenever(ctx.getStringAttribute("email")).thenReturn("user@test.com")
        val originalDetails = mock<LdapUserDetails>()

        val details = mapper.extendUserDetails(ctx, originalDetails)
        assertIs<ExtendedLDAPUserDetails>(details) {
            assertEquals("User", it.fullName)
            assertEquals("user@test.com", it.email)
        }
    }

    @Test
    fun `Custom attributes filled`() {
        settings = settings.withFullNameAttribute("fullName").withEmailAttribute("mail")
        val mapper = ConfigurableUserDetailsContextMapper(settings, ldapTemplate)

        val ctx = mock<DirContextOperations>()
        whenever(ctx.getStringAttribute("fullName")).thenReturn("User")
        whenever(ctx.getStringAttribute("mail")).thenReturn("user@test.com")
        val originalDetails = mock<LdapUserDetails>()

        val details = mapper.extendUserDetails(ctx, originalDetails)
        assertIs<ExtendedLDAPUserDetails>(details) {
            assertEquals("User", it.fullName)
            assertEquals("user@test.com", it.email)
        }
    }

    @Test
    fun `Collecting the groups`() {
        val mapper = ConfigurableUserDetailsContextMapper(settings, ldapTemplate)

        val ctx = mock<DirContextOperations>()
        whenever(ctx.getStringAttribute("cn")).thenReturn("User")
        whenever(ctx.getStringAttributes("memberOf")).thenReturn(arrayOf("cn=Admin,ou=Ontrack", "cn=Developer,ou=PRJ"))
        val originalDetails = mock<LdapUserDetails>()

        val details = mapper.extendUserDetails(ctx, originalDetails)
        assertIs<ExtendedLDAPUserDetails>(details) {
            assertEquals("User", it.fullName)
            assertEquals(
                    setOf("Admin", "Developer"),
                    it.groups
            )
        }
    }

    @Test
    fun `Collecting the groups using reverse link`() {
        settings = settings
                .withGroupSearchBase("ou=groups")
                .withGroupSearchFilter("(uniqueMember={0})")
        val mapper = ConfigurableUserDetailsContextMapper(settings, ldapTemplate)

        // No group from the user
        val ctx = mock<DirContextOperations>()
        whenever(ctx.getStringAttribute("cn")).thenReturn("User")
        whenever(ctx.getStringAttributes("memberOf")).thenReturn(emptyArray())
        val originalDetails = mock<LdapUserDetails>()
        whenever(originalDetails.dn).thenReturn("cn=user,ou=people")

        // Groups from the search
        whenever(ldapTemplate.searchForSingleAttributeValues(
                "ou=groups",
                "(uniqueMember={0})",
                arrayOf("cn=user,ou=people"),
                "cn"
        )).thenReturn(setOf("Admin", "Developer"))

        val details = mapper.extendUserDetails(ctx, originalDetails)
        assertIs<ExtendedLDAPUserDetails>(details) {
            assertEquals("User", it.fullName)
            assertEquals(
                    setOf("Admin", "Developer"),
                    it.groups
            )
        }
    }

    @Test
    fun `Collecting the groups with custom attribute`() {
        settings = settings.withGroupAttribute("group")
        val mapper = ConfigurableUserDetailsContextMapper(settings, ldapTemplate)

        val ctx = mock<DirContextOperations>()
        whenever(ctx.getStringAttribute("cn")).thenReturn("User")
        whenever(ctx.getStringAttributes("group")).thenReturn(arrayOf("cn=Admin,ou=Ontrack", "cn=Developer,ou=PRJ"))
        val originalDetails = mock<LdapUserDetails>()

        val details = mapper.extendUserDetails(ctx, originalDetails)
        assertIs<ExtendedLDAPUserDetails>(details) {
            assertEquals("User", it.fullName)
            assertEquals(
                    setOf("Admin", "Developer"),
                    it.groups
            )
        }
    }

    @Test
    fun `Collecting the groups with filter`() {
        settings = settings.withGroupFilter("Ontrack")
        val mapper = ConfigurableUserDetailsContextMapper(settings, ldapTemplate)

        val ctx = mock<DirContextOperations>()
        whenever(ctx.getStringAttribute("cn")).thenReturn("User")
        whenever(ctx.getStringAttributes("memberOf")).thenReturn(arrayOf("cn=Admin,ou=Ontrack", "cn=Developer,ou=PRJ"))
        val originalDetails = mock<LdapUserDetails>()

        val details = mapper.extendUserDetails(ctx, originalDetails)
        assertIs<ExtendedLDAPUserDetails>(details) {
            assertEquals("User", it.fullName)
            assertEquals(
                    setOf("Admin"),
                    it.groups
            )
        }
    }

}
