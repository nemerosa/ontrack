package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.dashboards.DashboardEdition
import net.nemerosa.ontrack.model.dashboards.DashboardSharing
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.SecuritySettings
import net.nemerosa.ontrack.repository.AccountGroupRepository
import net.nemerosa.ontrack.repository.RoleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull
import kotlin.reflect.KClass

@Service
@Transactional
class AccountACLServiceImpl(
    private val cachedSettingsService: CachedSettingsService,
    private val roleRepository: RoleRepository,
    private val rolesService: RolesService,
    private val accountGroupRepository: AccountGroupRepository,
    private val accountGroupContributors: List<AccountGroupContributor>,
) : AccountACLService {

    override fun getAuthorizations(account: Account): Authorisations {
        return Authorisations()
            .withProjectFunctions(autoProjectFunctions)
            .withGlobalFunctions(autoGlobalFunctions)
            .withGlobalRole(
                roleRepository.findGlobalRoleByAccount(account.id()).getOrNull()
                    ?.let { id: String -> rolesService.getGlobalRole(id).getOrNull() })
            .withProjectRoles(roleRepository.findProjectRoleAssociationsByAccount(account.id()) { project: Int, roleId: String ->
                rolesService.getProjectRoleAssociation(
                    project,
                    roleId
                )
            })
    }

    override val autoProjectFunctions: Set<KClass<out ProjectFunction>>
        get() {
            val settings = cachedSettingsService.getCachedSettings(SecuritySettings::class.java)
            return if (settings.isGrantProjectViewToAll) {
                if (settings.isGrantProjectParticipationToAll) {
                    setOf(
                        ProjectView::class,
                        ValidationRunStatusChange::class,
                        ValidationRunStatusCommentEditOwn::class
                    )
                } else {
                    setOf(ProjectView::class)
                }
            } else {
                emptySet()
            }
        }

    override val autoGlobalFunctions: Set<KClass<out GlobalFunction>>
        get() {
            val settings = cachedSettingsService.getCachedSettings(SecuritySettings::class.java)
            val functions = mutableSetOf<KClass<out GlobalFunction>>()
            if (settings.grantDashboardEditionToAll) {
                functions += DashboardEdition::class
                if (settings.grantDashboardSharingToAll) {
                    functions += DashboardSharing::class
                }
            }
            return functions.toSet()
        }

    override fun getGroups(account: Account): List<AuthorizedGroup> {
        // List of authenticated groups
        val groups = mutableListOf<AuthorizedGroup>()
        // Authorisations from groups
        groups.addAll(
            accountGroupRepository.findByAccount(account.id()).map {
                AuthorizedGroup(
                    group = it,
                    authorisations = getGroupACL(it)
                )
            }
        )
        // Authorisations from groups contributors
        groups.addAll(
            accountGroupContributors.flatMap { contributor ->
                contributor.collectGroups(account)
            }.map { group ->
                AuthorizedGroup(
                    group = group,
                    authorisations = getGroupACL(group)
                )
            }
        )
        // OK
        return groups
    }

    private fun getGroupACL(group: AccountGroup): Authorisations =
        Authorisations()
            // Global role
            .withGlobalRole(
                roleRepository.findGlobalRoleByGroup(group.id()).getOrNull()
                    ?.let { id: String -> rolesService.getGlobalRole(id).getOrNull() }
            ) // Project roles
            .withProjectRoles(
                roleRepository.findProjectRoleAssociationsByGroup(group.id()) { project: Int, roleId: String ->
                    rolesService.getProjectRoleAssociation(
                        project,
                        roleId,
                    )
                }
            )
}