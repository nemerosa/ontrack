package net.nemerosa.ontrack.extension.oidc.settings

import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

/**
 * Definition of an OIDC provider so that it's useable by Ontrack.
 *
 * @property id Unique ID for this provider
 * @property name Display name for this provider
 * @property description Tooltip for this provider
 * @property issuerId OIDC issueId URL
 * @property clientId OIDC client ID
 * @property clientSecret OIDC client secret
 */
data class OntrackOIDCProvider(
        @get:NotNull(message = "The account name is required.")
        @get:Pattern(regexp = "[a-zA-Z0-9_-]+", message = "The ID must contain only letters, digits, underscores and dashes.")
        val id: String,
        val name: String,
        val description: String,
        val issuerId: String,
        val clientId: String,
        val clientSecret: String
) {

    /**
     * Erases the [clientSecret].
     */
    fun obfuscate() = OntrackOIDCProvider(
            id, name, description, issuerId, clientId, ""
    )

}