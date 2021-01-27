package net.nemerosa.ontrack.model.security

import com.fasterxml.jackson.annotation.JsonProperty
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIName
import java.io.Serializable

/**
 * Authentication source for an account or group.
 */
@APIDescription("Authentication source for an account or group.")
data class AuthenticationSource(
        @APIDescription("Name of the provider for this authentication source")
        val provider: String,
        @APIDescription("Key for this source")
        val key: String,
        @APIDescription("Display name for this source")
        val name: String,
        @JsonProperty("enabled")
        @APIName("enabled")
        @APIDescription("Is this authentication source enabled?")
        val isEnabled: Boolean = true,
        @JsonProperty("allowingPasswordChange")
        @APIName("allowingPasswordChange")
        @APIDescription("Is this authentication source allowing to change a password?")
        val isAllowingPasswordChange: Boolean = false,
        @JsonProperty("groupMappingSupported")
        @APIName("groupMappingSupported")
        @APIDescription("Does this authentication source support external groups?")
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