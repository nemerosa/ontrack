package net.nemerosa.ontrack.model.security

import net.nemerosa.ontrack.model.structure.Entity
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.NameDescription
import java.io.Serializable

/**
 * Group of accounts.
 */
data class AccountGroup(
        override val id: ID,
        val name: String,
        val description: String?
) : Entity, Serializable {

    companion object {
        @JvmStatic
        fun of(name: String, description: String?) =
                AccountGroup(
                        ID.NONE,
                        name,
                        description
                )
    }


    fun withId(id: ID): AccountGroup {
        return AccountGroup(
                id,
                name,
                description
        )
    }

    fun update(input: NameDescription): AccountGroup {
        return AccountGroup(
                id,
                input.name,
                input.description
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
