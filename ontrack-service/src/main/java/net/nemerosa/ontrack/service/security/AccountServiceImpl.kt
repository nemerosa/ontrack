package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.exceptions.AccountDefaultAdminCannotDeleteException
import net.nemerosa.ontrack.model.exceptions.AccountDefaultAdminCannotUpdateNameException
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.Entity
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.repository.AccountGroupRepository
import net.nemerosa.ontrack.repository.AccountRepository
import net.nemerosa.ontrack.repository.RoleRepository
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*
import java.util.stream.Collectors

@Service
@Transactional
class AccountServiceImpl(
        private val roleRepository: RoleRepository,
        private val rolesService: RolesService,
        private val accountRepository: AccountRepository,
        private val accountGroupRepository: AccountGroupRepository,
        private val securityService: SecurityService,
        private val authenticationSourceService: AuthenticationSourceService
) : AccountService {

    // TODO #756 Cleanup
    private val passwordEncoder: PasswordEncoder = NoOpPasswordEncoder.getInstance()

    private var accountGroupContributors: Collection<AccountGroupContributor> = emptyList()

    @Autowired(required = false)
    fun setAccountGroupContributors(accountGroupContributors: Collection<AccountGroupContributor>) {
        this.accountGroupContributors = accountGroupContributors
    }

    override fun withACL(raw: AuthenticatedAccount): Account {
        return raw.account
                // Global role
                .withGlobalRole(
                        roleRepository.findGlobalRoleByAccount(raw.account.id()).getOrNull()?.let { id: String -> rolesService.getGlobalRole(id).getOrNull() }
                )
                // Project roles
                .withProjectRoles(
                        roleRepository.findProjectRoleAssociationsByAccount(raw.account.id()) { project: Int, roleId: String -> rolesService.getProjectRoleAssociation(project, roleId) }
                )
                // Groups from the repository
                .withGroups(
                        accountGroupRepository
                                .findByAccount(raw.account.id())
                                .map { group: AccountGroup -> groupWithACL(group) }
                )
                // Group contributions
                .withGroups(
                        accountGroupContributors
                                .flatMap { accountGroupContributor: AccountGroupContributor -> accountGroupContributor.collectGroups(raw) }
                                .map { group: AccountGroup -> groupWithACL(group) }
                )
                // OK
                .lock()
    }

    override fun getAccounts(): List<Account> {
        securityService.checkGlobalFunction(AccountManagement::class.java)
        return accountRepository
                .findAll { mode: String? -> authenticationSourceService.getAuthenticationSource(mode) }
                .map { account: Account -> account.withGroups(accountGroupRepository.findByAccount(account.id())) }
    }

    override fun create(input: AccountInput): Account {
        val account = create(input, "password")
        accountRepository.setPassword(account.id(), passwordEncoder.encode(input.password))
        return account
    }

    override fun create(input: AccountInput, authenticationSourceMode: String): Account {
        securityService.checkGlobalFunction(AccountManagement::class.java)
        // Creates the account
        var account = Account.of(
                input.name,
                input.fullName,
                input.email,
                SecurityRole.USER,
                authenticationSourceService.getAuthenticationSource(authenticationSourceMode)
        )
        // Saves it
        account = accountRepository.newAccount(account)
        // Account groups
        accountGroupRepository.linkAccountToGroups(account.id(), input.groups)
        // OK
        return account
    }

    override fun findUserByNameAndSource(username: String, sourceProvider: AuthenticationSourceProvider): Optional<Account> {
        securityService.checkGlobalFunction(AccountManagement::class.java)
        return accountRepository.findUserByNameAndSource(username, sourceProvider)
    }

    override fun updateAccount(accountId: ID, input: AccountInput): Account {
        securityService.checkGlobalFunction(AccountManagement::class.java)
        // Gets the existing account
        var account = getAccount(accountId)
        // Checks if default admin
        if (account.isDefaultAdmin && !StringUtils.equals(account.name, input.name)) {
            throw AccountDefaultAdminCannotUpdateNameException()
        }
        // Updates it
        account = account.update(input)
        // Saves it
        accountRepository.saveAccount(account)
        // Updating the password?
        if (StringUtils.isNotBlank(input.password)) {
            accountRepository.setPassword(accountId.value, passwordEncoder.encode(input.password))
        }
        // Account groups
        accountGroupRepository.linkAccountToGroups(account.id(), input.groups)
        // OK
        return getAccount(accountId)
    }

    override fun deleteAccount(accountId: ID): Ack { // Security check
        securityService.checkGlobalFunction(AccountManagement::class.java)
        // Check the `admin` account
        if (getAccount(accountId).isDefaultAdmin) {
            throw AccountDefaultAdminCannotDeleteException()
        }
        // Deletion
        return accountRepository.deleteAccount(accountId)
    }

    override fun getAccountGroups(): List<AccountGroup> {
        securityService.checkGlobalFunction(AccountGroupManagement::class.java)
        return accountGroupRepository.findAll()
    }

    override fun createGroup(nameDescription: NameDescription): AccountGroup {
        securityService.checkGlobalFunction(AccountGroupManagement::class.java)
        // Creates the account group
        val group = AccountGroup.of(nameDescription.name, nameDescription.description)
        // Saves it
        return accountGroupRepository.newAccountGroup(group)
    }

    override fun getAccountGroup(groupId: ID): AccountGroup {
        securityService.checkGlobalFunction(AccountGroupManagement::class.java)
        return accountGroupRepository.getById(groupId)
    }

    override fun updateGroup(groupId: ID, input: NameDescription): AccountGroup {
        securityService.checkGlobalFunction(AccountGroupManagement::class.java)
        val group = getAccountGroup(groupId).update(input)
        accountGroupRepository.update(group)
        return group
    }

    override fun deleteGroup(groupId: ID): Ack {
        securityService.checkGlobalFunction(AccountGroupManagement::class.java)
        return accountGroupRepository.delete(groupId)
    }

    override fun getAccountGroupsForSelection(accountId: ID): List<AccountGroupSelection> { // Account groups or none
        val accountGroupIds: Set<Int> = (accountId.ifSet { accountGroupRepository.findByAccount(it) } ?: emptyList())
                .map { obj: Entity -> obj.id() }
                .toSet()
        // Collection of groups with the selection
        return accountGroups
                .map { group: AccountGroup -> AccountGroupSelection(group, accountGroupIds.contains(group.id())) }
    }

    override fun searchPermissionTargets(token: String): Collection<PermissionTarget> {
        securityService.checkGlobalFunction(AccountManagement::class.java)
        val targets: MutableList<PermissionTarget> = ArrayList()
        // Users first
        targets.addAll(
                accountRepository.findByNameToken(token) { mode: String? -> authenticationSourceService.getAuthenticationSource(mode) }
                        .map { obj: Account -> obj.asPermissionTarget() }
        )
        // ... then groups
        targets.addAll(
                accountGroupRepository.findByNameToken(token)
                        .map { obj: AccountGroup -> obj.asPermissionTarget() }
        )
        // OK
        return targets
    }

    override fun saveGlobalPermission(type: PermissionTargetType, id: Int, input: PermissionInput): Ack {
        return when (type) {
            PermissionTargetType.ACCOUNT -> {
                securityService.checkGlobalFunction(AccountManagement::class.java)
                roleRepository.saveGlobalRoleForAccount(id, input.role)
            }
            PermissionTargetType.GROUP -> {
                securityService.checkGlobalFunction(AccountGroupManagement::class.java)
                roleRepository.saveGlobalRoleForGroup(id, input.role)
            }
            else -> Ack.NOK
        }
    }

    override fun getGlobalPermissions(): Collection<GlobalPermission> {
        val permissions: MutableCollection<GlobalPermission> = ArrayList()
        // Users first
        permissions.addAll(
                accountRepository.findAll { mode: String? -> authenticationSourceService.getAuthenticationSource(mode) }
                        .mapNotNull { account: Account -> getGlobalPermission(account) }
        )
        // ... then groups
        permissions.addAll(
                accountGroupRepository.findAll()
                        .mapNotNull { group: AccountGroup -> getGroupGlobalPermission(group) }
        )
        // OK
        return permissions
    }

    override fun deleteGlobalPermission(type: PermissionTargetType, id: Int): Ack {
        return when (type) {
            PermissionTargetType.ACCOUNT -> {
                securityService.checkGlobalFunction(AccountManagement::class.java)
                roleRepository.deleteGlobalRoleForAccount(id)
            }
            PermissionTargetType.GROUP -> {
                securityService.checkGlobalFunction(AccountGroupManagement::class.java)
                roleRepository.deleteGlobalRoleForGroup(id)
            }
            else -> Ack.NOK
        }
    }

    override fun getProjectPermissions(projectId: ID): Collection<ProjectPermission> {
        securityService.checkProjectFunction(projectId.value, ProjectAuthorisationMgt::class.java)
        val permissions: MutableCollection<ProjectPermission> = ArrayList()
        // Users first
        permissions.addAll(
                accountRepository.findAll { mode: String? -> authenticationSourceService.getAuthenticationSource(mode) }
                        .mapNotNull { account: Account -> getProjectPermission(projectId, account) }
        )
        // ... then groups
        permissions.addAll(
                accountGroupRepository.findAll()
                        .mapNotNull { accountGroup: AccountGroup -> getGroupProjectPermission(projectId, accountGroup) }
        )
        // OK
        return permissions
    }

    override fun saveProjectPermission(projectId: ID, type: PermissionTargetType, id: Int, input: PermissionInput): Ack {
        securityService.checkProjectFunction(projectId.value, ProjectAuthorisationMgt::class.java)
        return when (type) {
            PermissionTargetType.ACCOUNT -> roleRepository.saveProjectRoleForAccount(projectId.value, id, input.role)
            PermissionTargetType.GROUP -> roleRepository.saveProjectRoleForGroup(projectId.value, id, input.role)
            else -> Ack.NOK
        }
    }

    override fun deleteProjectPermission(projectId: ID, type: PermissionTargetType, id: Int): Ack {
        securityService.checkProjectFunction(projectId.value, ProjectAuthorisationMgt::class.java)
        return when (type) {
            PermissionTargetType.ACCOUNT -> roleRepository.deleteProjectRoleForAccount(projectId.value, id)
            PermissionTargetType.GROUP -> roleRepository.deleteProjectRoleForGroup(projectId.value, id)
            else -> Ack.NOK
        }
    }

    override fun getProjectPermissionsForAccount(account: Account): Collection<ProjectRoleAssociation> {
        return roleRepository.findProjectRoleAssociationsByAccount(
                account.id()) { project: Int?, roleId: String? -> rolesService.getProjectRoleAssociation(project!!, roleId!!) }
                .stream() // Filter by authorisation
                .filter { projectRoleAssociation: ProjectRoleAssociation ->
                    securityService.isProjectFunctionGranted(
                            projectRoleAssociation.projectId,
                            ProjectAuthorisationMgt::class.java
                    )
                } // OK
                .collect(Collectors.toList())
    }

    override fun getGlobalRoleForAccount(account: Account): Optional<GlobalRole> {
        return roleRepository.findGlobalRoleByAccount(account.id())
                .flatMap { id: String? -> rolesService.getGlobalRole(id!!) }
    }

    override fun getAccountsForGroup(accountGroup: AccountGroup): List<Account> {
        return accountRepository.getAccountsForGroup(accountGroup) { mode: String? -> authenticationSourceService.getAuthenticationSource(mode) }
    }

    override fun getGlobalRoleForAccountGroup(group: AccountGroup): Optional<GlobalRole> {
        return roleRepository.findGlobalRoleByGroup(group.id())
                .flatMap { id: String? -> rolesService.getGlobalRole(id!!) }
    }

    override fun getProjectPermissionsForAccountGroup(group: AccountGroup): Collection<ProjectRoleAssociation> {
        return roleRepository.findProjectRoleAssociationsByGroup(
                group.id()) { project: Int?, roleId: String? -> rolesService.getProjectRoleAssociation(project!!, roleId!!) }
                .stream() // Filter by authorisation
                .filter { projectRoleAssociation: ProjectRoleAssociation ->
                    securityService.isProjectFunctionGranted(
                            projectRoleAssociation.projectId,
                            ProjectAuthorisationMgt::class.java
                    )
                } // OK
                .collect(Collectors.toList())
    }

    override fun findAccountGroupsByGlobalRole(globalRole: GlobalRole): Collection<AccountGroup> {
        return roleRepository.findAccountGroupsByGlobalRole(globalRole) { groupId: ID -> getAccountGroup(groupId) }
    }

    override fun findAccountsByGlobalRole(globalRole: GlobalRole): Collection<Account> {
        return roleRepository.findAccountsByGlobalRole(globalRole) { accountId: ID -> getAccount(accountId) }
    }

    override fun findAccountGroupsByProjectRole(project: Project, projectRole: ProjectRole): Collection<AccountGroup> {
        return roleRepository.findAccountGroupsByProjectRole(project, projectRole) { groupId: ID -> getAccountGroup(groupId) }
    }

    override fun findAccountsByProjectRole(project: Project, projectRole: ProjectRole): Collection<Account> {
        return roleRepository.findAccountsByProjectRole(project, projectRole) { accountId: ID -> getAccount(accountId) }
    }

    private fun getGroupProjectPermission(projectId: ID, accountGroup: AccountGroup): ProjectPermission? {
        val roleAssociationOptional = roleRepository.findProjectRoleAssociationsByGroup(
                accountGroup.id(),
                projectId.value) { project: Int, roleId: String -> rolesService.getProjectRoleAssociation(project, roleId) }
        return if (roleAssociationOptional.isPresent) {
            ProjectPermission(
                    projectId,
                    accountGroup.asPermissionTarget(),
                    roleAssociationOptional.get().projectRole
            )
        } else {
            null
        }
    }

    private fun getProjectPermission(projectId: ID, account: Account): ProjectPermission? {
        val roleAssociationOptional = roleRepository.findProjectRoleAssociationsByAccount(
                account.id(),
                projectId.value
        ) { project: Int, roleId: String -> rolesService.getProjectRoleAssociation(project, roleId) }
        return if (roleAssociationOptional.isPresent) {
            ProjectPermission(
                    projectId,
                    account.asPermissionTarget(),
                    roleAssociationOptional.get().projectRole
            )
        } else {
            null
        }
    }

    private fun getGroupGlobalPermission(group: AccountGroup): GlobalPermission? {
        val roleId = roleRepository.findGlobalRoleByGroup(group.id())
        if (roleId.isPresent) {
            val globalRole = rolesService.getGlobalRole(roleId.get())
            if (globalRole.isPresent) {
                return GlobalPermission(
                        group.asPermissionTarget(),
                        globalRole.get()
                )
            }
        }
        return null
    }

    private fun getGlobalPermission(account: Account): GlobalPermission? {
        val roleId = roleRepository.findGlobalRoleByAccount(account.id())
        if (roleId.isPresent) {
            val globalRole = rolesService.getGlobalRole(roleId.get())
            if (globalRole.isPresent) {
                return GlobalPermission(
                        account.asPermissionTarget(),
                        globalRole.get()
                )
            }
        }
        return null
    }

    override fun getAccount(accountId: ID): Account {
        securityService.checkGlobalFunction(AccountManagement::class.java)
        return accountRepository.getAccount(accountId) { mode: String? -> authenticationSourceService.getAuthenticationSource(mode) }
                .withGroups(accountGroupRepository.findByAccount(accountId.value))
    }

    protected fun groupWithACL(group: AccountGroup): AccountGroup {
        return group // Global role
                .withGlobalRole(
                        roleRepository.findGlobalRoleByGroup(group.id()).getOrNull()?.let { id: String -> rolesService.getGlobalRole(id).getOrNull() }
                ) // Project roles
                .withProjectRoles(
                        roleRepository.findProjectRoleAssociationsByGroup(group.id()) { project: Int?, roleId: String? -> rolesService.getProjectRoleAssociation(project!!, roleId!!) }
                ) // OK
                .lock()
    }

}