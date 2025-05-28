package net.nemerosa.ontrack.model.security

import net.nemerosa.ontrack.model.structure.Entity
import net.nemerosa.ontrack.model.structure.ID
import java.io.Serializable

data class Account(
    override val id: ID,
    val fullName: String,
    val email: String,
    val role: SecurityRole,
) : Entity, Serializable {

    companion object {

        fun user(fullName: String, email: String) =
            Account(
                id = ID.NONE,
                fullName = fullName,
                email = email,
                role = SecurityRole.USER,
            )

        @JvmStatic
        fun of(fullName: String, email: String, role: SecurityRole) =
            Account(
                id = ID.NONE,
                fullName = fullName,
                email = email,
                role = role,
            )

    }

    fun withId(id: ID): Account = Account(
        id = id,
        fullName = fullName,
        email = email,
        role = role,
    )

    fun update(input: AccountInput) =
        Account(
            id = id,
            fullName = input.fullName,
            email = input.email,
            role = role,
        )

    fun asPermissionTarget() =
        PermissionTarget(
            type = PermissionTargetType.ACCOUNT,
            id = id(),
            name = email,
            description = fullName
        )

}
