package net.nemerosa.ontrack.model.security

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * Authentication source for an account or group.
 */
class AuthenticationSource(
        val id: String,
        val name: String,
        @JsonProperty("allowingPasswordChange")
        val isAllowingPasswordChange: Boolean = false
) : Serializable {

    fun withAllowingPasswordChange(isAllowingPasswordChange: Boolean) = AuthenticationSource(id, name, isAllowingPasswordChange)

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