package net.nemerosa.ontrack.it

import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.settings.SecuritySettings
import net.nemerosa.ontrack.model.settings.SettingsManagerService
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.Branch.Companion.of
import net.nemerosa.ontrack.model.structure.Build.Companion.of
import net.nemerosa.ontrack.model.structure.ID.Companion.of
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import net.nemerosa.ontrack.model.structure.Project.Companion.of
import net.nemerosa.ontrack.model.structure.PromotionRun.Companion.of
import net.nemerosa.ontrack.model.structure.Signature.Companion.of
import net.nemerosa.ontrack.test.TestUtils.uid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import java.util.concurrent.Callable


abstract class AbstractServiceTestSupport : AbstractITTestSupport() {

    @Autowired
    protected lateinit var accountService: AccountService

    @Autowired
    protected lateinit var structureService: StructureService

    @Autowired
    protected lateinit var propertyService: PropertyService

    @Autowired
    protected lateinit var settingsManagerService: SettingsManagerService

    @Autowired
    protected lateinit var cachedSettingsService: CachedSettingsService

    @Autowired
    protected lateinit var securityService: SecurityService

    @Autowired
    protected lateinit var rolesService: RolesService

    protected fun doCreateAccountGroup(): AccountGroup {
        return asUser().with(AccountGroupManagement::class.java).call {
            val name = uid("G")
            accountService.createGroup(
                    AccountGroupInput(name, "", false)
            )
        }
    }

    protected fun doCreateAccount(accountGroup: AccountGroup): Account {
        return doCreateAccount(listOf(accountGroup))
    }

    protected fun doCreateAccount(accountGroups: List<AccountGroup> = emptyList()): Account {
        return asUser().with(AccountManagement::class.java).call {
            val name = uid("A")
            accountService.create(
                    AccountInput(
                            name,
                            "Test $name",
                            "$name@test.com",
                            "test",
                            accountGroups.map { it.id() }
                    )
            )
        }
    }

    protected fun doCreateAccountWithGlobalRole(role: String): Account {
        val account = doCreateAccount()
        return asUser().with(AccountManagement::class.java).call {
            accountService.saveGlobalPermission(
                    PermissionTargetType.ACCOUNT,
                    account.id(),
                    PermissionInput(role)
            )
            account
        }
    }

    protected fun doCreateAccountWithProjectRole(project: Project, role: String): Account {
        val account = doCreateAccount()
        return asUser().with(project, ProjectAuthorisationMgt::class.java).call {
            accountService.saveProjectPermission(
                    project.id,
                    PermissionTargetType.ACCOUNT,
                    account.id(),
                    PermissionInput(role)
            )
            account
        }
    }

    protected fun doCreateAccountGroupWithGlobalRole(role: String): AccountGroup {
        val group = doCreateAccountGroup()
        return asUser().with(AccountGroupManagement::class.java).call {
            accountService.saveGlobalPermission(
                    PermissionTargetType.GROUP,
                    group.id(),
                    PermissionInput(role)
            )
            group
        }
    }

    protected fun <T> setProperty(projectEntity: ProjectEntity, propertyTypeClass: Class<out PropertyType<T>>, data: T) {
        asUser().with(projectEntity, ProjectEdit::class.java).execute(Runnable {
            propertyService.editProperty(
                    projectEntity,
                    propertyTypeClass,
                    data
            )
        }
        )
    }

    protected fun <T> deleteProperty(projectEntity: ProjectEntity?, propertyTypeClass: Class<out PropertyType<T>>) {
        asUser().with(projectEntity!!, ProjectEdit::class.java).execute(Runnable {
            propertyService.deleteProperty(
                    projectEntity,
                    propertyTypeClass
            )
        }
        )
    }

    protected fun <T> getProperty(projectEntity: ProjectEntity, propertyTypeClass: Class<out PropertyType<T>>): T {
        return asUser().with(projectEntity, ProjectEdit::class.java).call {
            propertyService.getProperty(
                    projectEntity,
                    propertyTypeClass
            ).value
        }
    }

    @JvmOverloads
    protected fun doCreateProject(nameDescription: NameDescription = nameDescription()): Project {
        return asUser().with(ProjectCreation::class.java).call {
            structureService.newProject(
                    of(nameDescription)
            )
        }
    }

    @JvmOverloads
    protected fun doCreateBranch(project: Project = doCreateProject(), nameDescription: NameDescription = nameDescription()): Branch {
        return asUser().with(project.id(), BranchCreate::class.java).call {
            structureService.newBranch(
                    of(project, nameDescription)
            )
        }
    }

    @JvmOverloads
    protected fun doCreateBuild(branch: Branch = doCreateBranch(), nameDescription: NameDescription = nameDescription(), signature: Signature = of("test")): Build {
        return asUser().with(branch.projectId(), BuildCreate::class.java).call {
            structureService.newBuild(
                    of(
                            branch,
                            nameDescription,
                            signature
                    )
            )
        }
    }

    @JvmOverloads
    fun doValidateBuild(
            build: Build,
            vs: ValidationStamp,
            statusId: ValidationRunStatusID?,
            runData: ValidationRunData<*>? = null
    ): ValidationRun {
        return asUser().withView(build).with(build, ValidationRunCreate::class.java).call {
            structureService.newValidationRun(
                    build,
                    ValidationRunRequest(
                            vs.name,
                            statusId,
                            runData?.descriptor?.id,
                            runData?.data,
                            null
                    )
            )
        }
    }

    fun doValidateBuild(build: Build, vsName: String, statusId: ValidationRunStatusID): ValidationRun {
        val vs = doCreateValidationStamp(build.branch, nd(vsName, ""))
        return doValidateBuild(build, vs, statusId)
    }

    @JvmOverloads
    protected fun doCreatePromotionLevel(branch: Branch = doCreateBranch(), nameDescription: NameDescription = nameDescription()): PromotionLevel {
        return asUser().with(branch.projectId(), PromotionLevelCreate::class.java).call {
            structureService.newPromotionLevel(
                    PromotionLevel.of(
                            branch,
                            nameDescription
                    )
            )
        }
    }

    protected fun doCreateValidationStamp(): ValidationStamp {
        return doCreateValidationStamp(doCreateBranch(), nameDescription())
    }

    protected fun doCreateValidationStamp(config: ValidationDataTypeConfig<*>?): ValidationStamp {
        return doCreateValidationStamp(doCreateBranch(), nameDescription(), config)
    }

    @JvmOverloads
    fun doCreateValidationStamp(branch: Branch, nameDescription: NameDescription, config: ValidationDataTypeConfig<*>? = null): ValidationStamp {
        return asUser().with(branch.project.id(), ValidationStampCreate::class.java).call {
            structureService.newValidationStamp(
                    ValidationStamp.of(
                            branch,
                            nameDescription
                    ).withDataType(config)
            )
        }
    }

    protected fun doPromote(build: Build, promotionLevel: PromotionLevel, description: String?): PromotionRun {
        return asUser().with(build.projectId(), PromotionRunCreate::class.java).call {
            structureService.newPromotionRun(
                    of(
                            build,
                            promotionLevel,
                            of("test"),
                            description
                    )
            )
        }
    }

    protected fun <T> doSetProperty(entity: ProjectEntity, propertyType: Class<out PropertyType<T>>, data: T) {
        asUser().with(entity, ProjectEdit::class.java).call {
            propertyService.editProperty(
                    entity,
                    propertyType,
                    data
            )
        }
    }

    protected fun asUser(): UserCall = UserCall()

    protected fun asAdmin(): AdminCall = AdminCall()

    protected fun asAnonymous(): AnonymousCall {
        return AnonymousCall()
    }

    protected fun asUserWithView(vararg entities: ProjectEntity): ConfigurableAccountCall {
        var user: ConfigurableAccountCall = asUser()
        for (entity in entities) {
            user = user.withView(entity)
        }
        return user
    }

    protected fun asFixedAccount(account: Account): AccountCall<*> {
        return FixedAccountCall(account)
    }

    protected fun <T> asFixedAccount(account: Account, code: () -> T): T = asFixedAccount(account).call(code)

    protected fun asConfigurableAccount(account: Account): ConfigurableAccountCall {
        return ConfigurableAccountCall(account)
    }

    protected fun asGlobalRole(role: String): AccountCall<*> {
        return FixedAccountCall(doCreateAccountWithGlobalRole(role))
    }

    protected fun <T> asGlobalRole(role: String, code: () -> T): T = asGlobalRole(role).call(code)

    protected fun <T> view(projectEntity: ProjectEntity, callable: Callable<T>): T {
        return asUser().with(projectEntity.projectId(), ProjectView::class.java).call { callable.call() }
    }

    /**
     * This must always be called from [withGrantViewToAll] or [withNoGrantViewToAll].
     */
    private fun grantViewToAll(grantViewToAll: Boolean): Boolean = asUser().with(GlobalSettings::class.java).call {
        val old = cachedSettingsService.getCachedSettings(SecuritySettings::class.java).isGrantProjectViewToAll
        settingsManagerService.saveSettings(
                SecuritySettings(isGrantProjectViewToAll = grantViewToAll)
        )
        old
    }

    private fun <T> withGrantViewToAll(grantViewToAll: Boolean, task: () -> T): T {
        val old = grantViewToAll(grantViewToAll)
        return try {
            task()
        } finally {
            grantViewToAll(old)
        }
    }

    protected fun <T> withGrantViewToAll(task: () -> T): T = withGrantViewToAll(true, task)

    protected fun <T> withNoGrantViewToAll(task: () -> T): T = withGrantViewToAll(false, task)

    protected interface ContextCall {
        fun <T> call(call: () -> T): T
    }

    protected abstract class AbstractContextCall : ContextCall {

        override fun <T> call(call: () -> T): T {
            // Gets the current context
            val oldContext = SecurityContextHolder.getContext()
            return try {
                // Sets the new context
                contextSetup()
                // Call
                call()
            } finally {
                // Restores the context
                SecurityContextHolder.setContext(oldContext)
            }
        }

        fun execute(task: () -> Unit) = call(task)

        fun execute(task: Runnable) {
            call {
                task.run()
            }
        }

        protected abstract fun contextSetup()
    }

    protected class AnonymousCall : AbstractContextCall() {
        override fun contextSetup() {
            val context: SecurityContext = SecurityContextImpl()
            context.authentication = null
            SecurityContextHolder.setContext(context)
        }
    }

    protected open inner class AccountCall<T : AccountCall<T>>(
            protected val account: Account
    ) : AbstractContextCall() {

        override fun contextSetup() {
            val context: SecurityContext = SecurityContextImpl()
            val ontrackAuthenticatedUser = createOntrackAuthenticatedUser()
            val authentication = TestingAuthenticationToken(
                    ontrackAuthenticatedUser,
                    "",
                    account.role.name
            )
            context.authentication = authentication
            SecurityContextHolder.setContext(context)
        }

        protected open fun createOntrackAuthenticatedUser(): OntrackAuthenticatedUser =
                accountService.withACL(TestOntrackUser(account))

    }

    protected inner class FixedAccountCall(account: Account) : AccountCall<FixedAccountCall>(account)

    protected open inner class ConfigurableAccountCall(
            account: Account
    ) : AccountCall<ConfigurableAccountCall>(account) {

        /**
         * Global function associated to any global role to create
         */
        private val globalFunctions = mutableSetOf<Class<out GlobalFunction>>()

        /**
         * Project function associated to any project role to create
         */
        private val projectFunctions = mutableMapOf<Int, MutableSet<Class<out ProjectFunction>>>()

        /**
         * Associates a list of global functions to this account
         */
        @SafeVarargs
        fun with(vararg fn: Class<out GlobalFunction>): ConfigurableAccountCall {
            globalFunctions.addAll(fn)
            return this
        }

        /**
         * Associates a list of project functions for a given project to this account
         */
        fun with(projectId: Int, fn: Class<out ProjectFunction>): ConfigurableAccountCall {
            val projectFns = projectFunctions.getOrPut(projectId) { mutableSetOf() }
            projectFns.add(fn)
            return this
        }

        /**
         * Associates a list of project functions for a given project (designated by the [entity][e]) to this account
         */
        fun with(e: ProjectEntity, fn: Class<out ProjectFunction>): ConfigurableAccountCall {
            return with(e.projectId(), fn)
        }

        /**
         * Grants the [ProjectView] function to this account and the project designated by the [entity][e].
         */
        fun withView(e: ProjectEntity): ConfigurableAccountCall {
            return with(e, ProjectView::class.java)
        }

        override fun contextSetup() {
            val context: SecurityContext = SecurityContextImpl()
            val ontrackAuthenticatedUser = createOntrackAuthenticatedUser()
            val authentication = TestingAuthenticationToken(
                    ontrackAuthenticatedUser,
                    "",
                    account.role.name
            )
            context.authentication = authentication
            SecurityContextHolder.setContext(context)
        }

        override fun createOntrackAuthenticatedUser(): OntrackAuthenticatedUser {
            // Configures the account
            securityService.asAdmin {
                // Creating a global role if some global functions are required
                if (globalFunctions.isNotEmpty()) {
                    val globalRoleId = uid("GR")
                    rolesService.registerGlobalRole(
                            id = globalRoleId,
                            name = "Test role $globalRoleId",
                            description = "Test role $globalRoleId",
                            globalFunctions = globalFunctions.toList(),
                            projectFunctions = emptyList()
                    )
                    accountService.saveGlobalPermission(
                            PermissionTargetType.ACCOUNT,
                            account.id(),
                            PermissionInput(globalRoleId)
                    )
                }
                // Project permissions
                projectFunctions.forEach { (projectId, functions) ->
                    if (functions.isNotEmpty()) {
                        val projectRoleId = uid("PR")
                        rolesService.registerProjectRole(
                                id = projectRoleId,
                                name = "Test role $projectRoleId",
                                description = "Test role $projectRoleId",
                                projectFunctions = functions.toList()
                        )
                        accountService.saveProjectPermission(
                                of(projectId),
                                PermissionTargetType.ACCOUNT,
                                account.id(),
                                PermissionInput(projectRoleId)
                        )
                    }
                }
            }
            // Loading the account
            return super.createOntrackAuthenticatedUser()
        }
    }

    protected inner class UserCall : ConfigurableAccountCall(
            securityService.asAdmin {
                val name = uid("U")
                accountService.create(
                        AccountInput(
                                name,
                                "$name von Test",
                                "$name@test.com",
                                "xxx",
                                emptyList()
                        )
                )
            }
    )

    protected inner class AdminCall : AccountCall<AdminCall>(
            // Loading the predefined admin account
            securityService.asAdmin {
                accountService.getAccount(of(1))
            }
    )
}

private class TestOntrackUser(
        private val account: Account
) : OntrackUser {

    override val accountId: Int = account.id()

    override fun getAuthorities(): Collection<GrantedAuthority> =
            AuthorityUtils.createAuthorityList(account.role.roleName)

    override fun isEnabled(): Boolean = true

    override fun getUsername(): String = account.name

    override fun isCredentialsNonExpired(): Boolean = true

    override fun getPassword(): String = ""

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

}