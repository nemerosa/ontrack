package net.nemerosa.ontrack.model.security

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

/**
 * Authentication source for an account or group.
 */
data class AuthenticationSource(
        val provider: String,
        val key: String,
        val name: String,
        @JsonProperty("enabled")
        val isEnabled: Boolean = true,
        @JsonProperty("allowingPasswordChange")
        val isAllowingPasswordChange: Boolean = false,
        @JsonProperty("groupMappingSupported")
        val isGroupMappingSupported: Boolean = false
) : Serializable {

    override fun toString(): String = "$provider::$key"

    infix fun sameThan(other: AuthenticationSource) = provider == other.provider && key == other.key

    fun enabled(enabled: Boolean) = AuthenticationSource(
            provider = provider,
            key = key,
            name = name,
            isEnabled = enabled,
            isAllowingPasswordChange = isAllowingPasswordChange,
            isGroupMappingSupported = isGroupMappingSupported
    )

    companion object {
        /**
         * Authentication source used for tests
         */
        @JvmStatic
        fun none() = AuthenticationSource("none", "none", "Not defined")
    }

}