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
        val role: SecurityRole,
        val disabled: Boolean,
        val locked: Boolean,
) : Entity, Serializable {

    companion object {

        @JvmStatic
        fun of(name: String, fullName: String, email: String, role: SecurityRole, authenticationSource: AuthenticationSource, disabled: Boolean, locked: Boolean) =
                Account(
                        ID.NONE,
                        name,
                        fullName,
                        email,
                        authenticationSource,
                        role,
                        disabled = disabled,
                        locked = locked,
                )

    }

    fun withId(id: ID): Account = Account(
            id,
            name,
            fullName,
            email,
            authenticationSource,
            role,
            disabled = disabled,
            locked = locked,
    )

    fun update(input: AccountInput) =
            Account(
                    id,
                    input.name,
                    input.fullName,
                    input.email,
                    authenticationSource,
                    role,
                    disabled = input.disabled,
                    locked = input.locked,
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
