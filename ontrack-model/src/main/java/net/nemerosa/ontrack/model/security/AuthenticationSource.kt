package net.nemerosa.ontrack.model.security

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * Authentication source for an account or group.
 */
data class AuthenticationSource(
        val id: String,
        val name: String,
        @JsonProperty("allowingPasswordChange")
        val isAllowingPasswordChange: Boolean = false,
        @JsonProperty("groupMappingSupported")
        val isGroupMappingSupported: Boolean = false
) : Serializable {

    companion object {
        /**
         * Authentication source used for tests
         */
        @JvmStatic
        fun none() = AuthenticationSource("none", "Not defined")
    }

}