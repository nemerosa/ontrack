package net.nemerosa.ontrack.model.security

import net.nemerosa.ontrack.model.structure.Entity
import net.nemerosa.ontrack.model.structure.ID
import java.io.Serializable

data class Account(
    override val id: ID,
    val name: String,
    val fullName: String,
    val email: String,
    val role: SecurityRole,
) : Entity, Serializable {

    companion object {

        fun user(name: String, fullName: String, email: String) =
            Account(
                id = ID.NONE,
                name = name,
                fullName = fullName,
                email = email,
                role = SecurityRole.USER,
            )

        @JvmStatic
        fun of(name: String, fullName: String, email: String, role: SecurityRole) =
            Account(
                id = ID.NONE,
                name = name,
                fullName = fullName,
                email = email,
                role = role,
            )

    }

    fun withId(id: ID): Account = Account(
        id = id,
        name = name,
        fullName = fullName,
        email = email,
        role = role,
    )

    fun update(input: AccountInput) =
        Account(
            id = id,
            name = input.name,
            fullName = input.fullName,
            email = input.email,
            role = role,
        )

    fun asPermissionTarget() =
        PermissionTarget(
            type = PermissionTargetType.ACCOUNT,
            id = id(),
            name = name,
            description = fullName
        )

}
