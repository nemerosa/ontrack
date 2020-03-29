package net.nemerosa.ontrack.model.security

import net.nemerosa.ontrack.model.structure.Entity
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.NameDescription
import java.io.Serializable

/**
 * Group of accounts.
 */
open class AccountGroup(
        override val id: ID,
        val name: String,
        val description: String?,
        private var authorisations: Authorisations = Authorisations.none(),
        private val locked: Boolean
) : Entity, Serializable {

    fun isGranted(fn: Class<out GlobalFunction>) = authorisations.isGranted(fn)

    fun isGranted(projectId: Int, fn: Class<out ProjectFunction>) = authorisations.isGranted(projectId, fn)

    private fun checkLock() {
        if (locked) {
            throw IllegalStateException("Account is locked")
        }
    }

    fun withGlobalRole(globalRole: GlobalRole?): AccountGroup {
        checkLock()
        authorisations = authorisations.withGlobalRole(globalRole)
        return this
    }

    fun withProjectRoles(projectRoleAssociations: Collection<ProjectRoleAssociation>): AccountGroup {
        checkLock()
        authorisations = authorisations.withProjectRoles(projectRoleAssociations)
        return this
    }

    fun withProjectRole(projectRoleAssociation: ProjectRoleAssociation): AccountGroup {
        checkLock()
        authorisations = authorisations.withProjectRole(projectRoleAssociation)
        return this
    }

    fun lock(): AccountGroup =
            AccountGroup(
                    id,
                    name,
                    description,
                    authorisations,
                    true
            )

    companion object {
        @JvmStatic
        fun of(name: String, description: String?) =
                AccountGroup(
                        ID.NONE,
                        name,
                        description,
                        Authorisations.none(),
                        false
                )
    }


    fun withId(id: ID): AccountGroup {
        checkLock()
        return AccountGroup(
                id,
                name,
                description,
                authorisations,
                locked
        )
    }

    fun update(input: NameDescription): AccountGroup {
        checkLock()
        return AccountGroup(
                id,
                input.name,
                input.description,
                authorisations,
                locked
        )
    }

    fun asPermissionTarget() =
            PermissionTarget(
                    PermissionTargetType.GROUP,
                    id(),
                    name,
                    description
            )
}
