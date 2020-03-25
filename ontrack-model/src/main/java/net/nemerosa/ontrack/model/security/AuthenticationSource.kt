package net.nemerosa.ontrack.model.security

import java.io.Serializable

/**
 * Authentication source for an account or group.
 */
class AuthenticationSource(
        val id: String,
        val name: String,
        val allowingPasswordChange: Boolean = false
) : Serializable {

    fun withAllowingPasswordChange(allowingPasswordChange: Boolean) = AuthenticationSource(id, name, allowingPasswordChange)

    companion object {

        @JvmStatic
        fun of(id: String, name: String) = AuthenticationSource(id, name, false)

        /**
         * Authentication source used for tests
         */
        @JvmStatic
        fun none(): AuthenticationSource = of("none", "Not defined")
    }
}