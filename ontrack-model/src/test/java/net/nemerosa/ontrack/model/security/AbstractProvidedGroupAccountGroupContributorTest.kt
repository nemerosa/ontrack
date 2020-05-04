package net.nemerosa.ontrack.model.security

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import net.nemerosa.ontrack.model.structure.ID
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class AbstractProvidedGroupAccountGroupContributorTest {

    private lateinit var providedGroupsService: ProvidedGroupsService
    private val authenticationSource = AuthenticationSource.none()
    private lateinit var accountGroupMappingService: AccountGroupMappingService
    private lateinit var contributor: AbstractProvidedGroupAccountGroupContributor

    private val account = Account(
            ID.of(1), "user", "User", "user@test.com", AuthenticationSource.none(), SecurityRole.USER
    )

    private val adminGroup = AccountGroup(
            ID.of(1), "Administrators", "", false
    )
    private val readOnlyGroup = AccountGroup(
            ID.of(2), "Read-Only", "", false
    )
    private val participantGroup = AccountGroup(
            ID.of(3), "Participant", "", false
    )

    @Before
    fun prepare() {
        providedGroupsService = mock()
        accountGroupMappingService = mock()
        contributor = TestProvidedGroupAccountGroupContributor(providedGroupsService, authenticationSource, accountGroupMappingService)
    }

    @Test
    fun `Getting the groups`() {
        whenever(providedGroupsService.getProvidedGroups(1, authenticationSource)).thenReturn(setOf("org-admins", "org-users"))
        whenever(accountGroupMappingService.getGroups(AuthenticationSource.none(), "org-admins")).thenReturn(
                setOf(adminGroup)
        )
        whenever(accountGroupMappingService.getGroups(AuthenticationSource.none(), "org-users")).thenReturn(
                setOf(readOnlyGroup, participantGroup)
        )

        val groups = contributor.collectGroups(account)

        assertEquals(
                setOf(adminGroup, readOnlyGroup, participantGroup),
                groups.toSet()
        )
    }

    class TestProvidedGroupAccountGroupContributor(
            providedGroupsService: ProvidedGroupsService,
            authenticationSource: AuthenticationSource,
            accountGroupMappingService: AccountGroupMappingService
    ) : AbstractProvidedGroupAccountGroupContributor(
            providedGroupsService,
            authenticationSource,
            accountGroupMappingService
    )

}