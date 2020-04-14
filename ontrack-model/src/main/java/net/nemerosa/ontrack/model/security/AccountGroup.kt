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
        val description: String?,
        val autoJoin: Boolean
) : Entity, Serializable {

    fun withId(id: ID): AccountGroup {
        return AccountGroup(
                id,
                name,
                description,
                autoJoin
        )
    }

    fun update(input: AccountGroupInput) = AccountGroup(
            id = id,
            name = input.name,
            description = input.description,
            autoJoin = input.autoJoin
    )

    fun asPermissionTarget() =
            PermissionTarget(
                    PermissionTargetType.GROUP,
                    id(),
                    name,
                    description
            )
}
