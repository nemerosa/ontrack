package net.nemerosa.ontrack.model.security

import com.fasterxml.jackson.annotation.JsonProperty
import net.nemerosa.ontrack.model.structure.Entity
import net.nemerosa.ontrack.model.structure.ID
import java.io.Serializable

data class Account(
        override val id: ID,
        val name: String,
        val fullName: String,
        val email: String,
        val authenticationSource: AuthenticationSource,
        val role: SecurityRole
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
                        role
                )

    }

    fun withId(id: ID): Account = Account(
            id,
            name,
            fullName,
            email,
            authenticationSource,
            role
    )

    fun update(input: AccountInput) =
            Account(
                    id,
                    input.name,
                    input.fullName,
                    input.email,
                    authenticationSource,
                    role
            )

    /**
     * Default built-in admin?
     */
    @get:JsonProperty("defaultAdmin")
    val isDefaultAdmin
        get() = "admin" == name

    fun asPermissionTarget() =
            PermissionTarget(
                    PermissionTargetType.ACCOUNT,
                    id(),
                    name,
                    fullName
            )

}
