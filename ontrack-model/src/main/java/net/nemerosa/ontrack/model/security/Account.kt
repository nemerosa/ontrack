package net.nemerosa.ontrack.model.security

import net.nemerosa.ontrack.model.structure.Entity
import net.nemerosa.ontrack.model.structure.ID
import java.io.Serializable

data class Account(
        override val id: ID,
        val name: String,
        val fullName: String,
        val email: String,
        val authenticationSource: AuthenticationSource,
        val role: SecurityRole,
        private var groups: MutableList<AccountGroup>,
        private var authorisations: Authorisations,
        private val locked: Boolean
) : Entity, Serializable {

    companion object {

        @JvmStatic
        fun of(name: String, fullName: String, email: String, role: SecurityRole, authenticationSource: AuthenticationSource) =
                Account(
                        ID.NONE,
                        name,
                        fullName,
                        email,
                        authenticationSource,
                        role,
                        mutableListOf(),
                        Authorisations.none(),
                        false
                )

    }

    val accountGroups: List<AccountGroup> get() = groups.toList()

    fun isGranted(fn: Class<out GlobalFunction>) =
            (SecurityRole.ADMINISTRATOR == role)
                    || groups.any { it.isGranted(fn) }
                    || authorisations.isGranted(fn)

    fun isGranted(projectId: Int, fn: Class<out ProjectFunction>) =
            SecurityRole.ADMINISTRATOR == role
                    || groups.any { it.isGranted(projectId, fn) }
                    || authorisations.isGranted(projectId, fn)

    fun withId(id: ID): Account {
        checkLock()
        return Account(
                id,
                name,
                fullName,
                email,
                authenticationSource,
                role,
                groups,
                authorisations,
                locked
        )
    }

    fun lock() = Account(
            id,
            name,
            fullName,
            email,
            authenticationSource,
            role,
            groups,
            authorisations,
            true
    )

    fun update(input: AccountInput) =
            Account(
                    id,
                    input.name,
                    input.fullName,
                    input.email,
                    authenticationSource,
                    role,
                    groups,
                    authorisations,
                    locked
            )

    private fun checkLock() {
        if (locked) {
            throw IllegalStateException("Account is locked")
        }
    }

    fun withGroup(accountGroup: AccountGroup): Account {
        checkLock()
        this.groups.add(accountGroup)
        return this
    }

    fun withGroups(groups: Collection<AccountGroup>): Account {
        checkLock()
        this.groups.addAll(groups)
        return this
    }

    fun withGlobalRole(globalRole: GlobalRole?): Account {
        checkLock()
        authorisations = authorisations.withGlobalRole(globalRole)
        return this
    }

    fun withProjectRoles(projectRoleAssociations: Collection<ProjectRoleAssociation>): Account {
        checkLock()
        authorisations = authorisations.withProjectRoles(projectRoleAssociations)
        return this
    }

    fun withProjectRole(projectRoleAssociation: ProjectRoleAssociation): Account {
        checkLock()
        authorisations = authorisations.withProjectRole(projectRoleAssociation)
        return this
    }

    /**
     * Default built-in admin?
     */
    val isDefaultAdmin = "admin" == name

    fun asPermissionTarget() =
            PermissionTarget(
                    PermissionTargetType.ACCOUNT,
                    id(),
                    name,
                    fullName
            )

}
