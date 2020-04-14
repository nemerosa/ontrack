package net.nemerosa.ontrack.it

import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.security.Account.Companion.of
import net.nemerosa.ontrack.model.security.AuthenticationSource.Companion.none
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.Branch.Companion.of
import net.nemerosa.ontrack.model.structure.Build.Companion.of
import net.nemerosa.ontrack.model.structure.ID.Companion.of
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import net.nemerosa.ontrack.model.structure.Project.Companion.of
import net.nemerosa.ontrack.model.structure.PromotionRun.Companion.of
import net.nemerosa.ontrack.model.structure.Signature.Companion.of
import net.nemerosa.ontrack.test.TestUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.core.userdetails.UserDetails
import java.util.concurrent.Callable

abstract class AbstractServiceTestSupport : AbstractITTestSupport() {

    @Autowired
    protected lateinit var accountService: AccountService

    @Autowired
    protected lateinit var structureService: StructureService

    @Autowired
    protected lateinit var propertyService: PropertyService

    protected fun doCreateAccountGroup(): AccountGroup {
        return asUser().with(AccountGroupManagement::class.java).call {
            val name = TestUtils.uid("G")
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
            val name = TestUtils.uid("A")
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

    protected fun asUserWithView(vararg entities: ProjectEntity): UserCall {
        var user = asUser()
        for (entity in entities) {
            user = user.withView(entity)
        }
        return user
    }

    protected fun asAccount(account: Account): AccountCall<*> {
        return ProvidedAccountCall(account)
    }


    protected fun asGlobalRole(role: String): AccountCall<*> {
        return ProvidedAccountCall(doCreateAccountWithGlobalRole(role))
    }

    protected fun <T> view(projectEntity: ProjectEntity, callable: Callable<T>): T {
        return asUser().with(projectEntity.projectId(), ProjectView::class.java).call { callable.call() }
    }

    fun grantViewToAll(grantViewToAll: Boolean): Boolean {
        TODO("Restore the global settings")
    }

    protected fun <T> withGrantViewToAll(task: () -> T): T {
        val old = grantViewToAll(true)
        try {
            return task()
        } finally {
            grantViewToAll(old)
        }
    }

    protected fun <T> withNoGrantViewToAll(task: () -> T): T {
        val old = grantViewToAll(false)
        return try {
            task()
        } finally {
            grantViewToAll(old)
        }
    }

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

    protected open class AccountCall<T : AccountCall<T>>(
            protected val account: Account,
            private var authorisations: Authorisations = Authorisations()
    ) : AbstractContextCall() {

        constructor(name: String, role: SecurityRole) : this(of(name, name, "$name@test.com", role, none())) {}

        fun with(vararg fn: Class<out GlobalFunction>): T {
            authorisations = authorisations.withGlobalRole(
                    GlobalRole(
                            "test", "Test global role", "",
                            fn.toSet(),
                            emptySet()
                    )
            )
            @Suppress("UNCHECKED_CAST")
            return this as T
        }

        fun with(projectId: Int, fn: Class<out ProjectFunction>): T {
            authorisations = authorisations.withProjectRole(
                    ProjectRoleAssociation(
                            projectId,
                            ProjectRole(
                                    "test", "Test", "",
                                    setOf(fn)
                            )
                    )
            )
            @Suppress("UNCHECKED_CAST")
            return this as T
        }

        fun with(e: ProjectEntity, fn: Class<out ProjectFunction>): T {
            return with(e.projectId(), fn)
        }

        fun withView(e: ProjectEntity): T {
            return with(e, ProjectView::class.java)
        }

        override fun contextSetup() {
            val context: SecurityContext = SecurityContextImpl()
            val authentication = TestingAuthenticationToken(
                    TestOntrackAuthenticatedUser(account, authorisations),
                    "",
                    account.role.name
            )
            context.authentication = authentication
            SecurityContextHolder.setContext(context)
        }

    }

    protected class ProvidedAccountCall(account: Account) : AccountCall<ProvidedAccountCall>(account)

    protected class UserCall : AccountCall<UserCall>("user", SecurityRole.USER) {
        fun withId(id: Int): AccountCall<UserCall> {
            return AccountCall(account.withId(of(id)))
        }
    }

    protected class AdminCall : AccountCall<AdminCall>("admin", SecurityRole.ADMINISTRATOR)
}

private class TestOntrackAuthenticatedUser(
        override val account: Account,
        private val authorisations: Authorisations,
        override val user: OntrackUser = TestOntrackUser(account)
) : OntrackAuthenticatedUser, UserDetails by user {

    override fun isGranted(fn: Class<out GlobalFunction>): Boolean = authorisations.isGranted(fn)

    override fun isGranted(projectId: Int, fn: Class<out ProjectFunction>): Boolean = authorisations.isGranted(projectId, fn)
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