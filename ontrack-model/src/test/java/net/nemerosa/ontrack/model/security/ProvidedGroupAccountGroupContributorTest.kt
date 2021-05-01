package net.nemerosa.ontrack.model.security

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import net.nemerosa.ontrack.model.structure.ID
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ProvidedGroupAccountGroupContributorTest {

    private lateinit var providedGroupsService: ProvidedGroupsService
    private lateinit var accountGroupMappingService: AccountGroupMappingService
    private lateinit var contributor: ProvidedGroupAccountGroupContributor

    private val adminGroup = AccountGroup(
            ID.of(1), "Administrators", ""
    )
    private val readOnlyGroup = AccountGroup(
            ID.of(2), "Read-Only", ""
    )
    private val participantGroup = AccountGroup(
            ID.of(3), "Participant", ""
    )

    @Before
    fun prepare() {
        providedGroupsService = mock()
        accountGroupMappingService = mock()
        contributor = ProvidedGroupAccountGroupContributor(providedGroupsService, accountGroupMappingService)
    }

    @Test
    fun `Getting the groups`() {
        val authenticationSource = AuthenticationSource(
                provider = "test",
                key = "",
                name = "Testing",
                isEnabled = true,
                isGroupMappingSupported = true,
                isAllowingPasswordChange = false
        )
        val account = createAccount(authenticationSource)
        whenever(providedGroupsService.getProvidedGroups(1, authenticationSource)).thenReturn(setOf("org-admins", "org-users"))
        whenever(accountGroupMappingService.getGroups(authenticationSource, "org-admins")).thenReturn(
                setOf(adminGroup)
        )
        whenever(accountGroupMappingService.getGroups(authenticationSource, "org-users")).thenReturn(
                setOf(readOnlyGroup, participantGroup)
        )

        val groups = contributor.collectGroups(account)

        assertEquals(
                setOf(adminGroup, readOnlyGroup, participantGroup),
                groups.toSet()
        )
    }

    @Test
    fun `Getting the groups when not enabled`() {
        val authenticationSource = AuthenticationSource(
                provider = "test",
                key = "",
                name = "Testing",
                isEnabled = false,
                isGroupMappingSupported = true,
                isAllowingPasswordChange = false
        )
        val account = createAccount(authenticationSource)
        val groups = contributor.collectGroups(account)
        assertTrue(groups.isEmpty())
    }

    @Test
    fun `Getting the groups when mapping not supported`() {
        val authenticationSource = AuthenticationSource(
                provider = "test",
                key = "",
                name = "Testing",
                isEnabled = true,
                isGroupMappingSupported = false,
                isAllowingPasswordChange = false
        )
        val account = createAccount(authenticationSource)
        val groups = contributor.collectGroups(account)
        assertTrue(groups.isEmpty())
    }

    private fun createAccount(authenticationSource: AuthenticationSource): Account {
        return Account(
                ID.of(1), "user", "User", "user@test.com", authenticationSource, SecurityRole.USER,
                disabled = false,
                locked = false,
        )
    }

}