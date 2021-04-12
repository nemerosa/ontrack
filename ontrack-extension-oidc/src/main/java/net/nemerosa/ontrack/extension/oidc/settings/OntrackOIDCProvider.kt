package net.nemerosa.ontrack.extension.oidc.settings

import net.nemerosa.ontrack.model.annotations.APIDescription
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
 * @property groupFilter Regular expression used to filter groups associated with the OIDC user
 */
data class OntrackOIDCProvider(
        @get:NotNull(message = "The account name is required.")
        @get:Pattern(regexp = "[a-zA-Z0-9_-]+", message = "The ID must contain only letters, digits, underscores and dashes.")
        @APIDescription("Unique ID for this provider")
        val id: String,
        @APIDescription("Display name for this provider")
        val name: String,
        @APIDescription("Tooltip for this provider")
        val description: String,
        @APIDescription("OIDC issueId URL")
        val issuerId: String,
        @APIDescription("OIDC client ID")
        val clientId: String,
        @APIDescription("OIDC client secret")
        val clientSecret: String,
        @APIDescription("Regular expression used to filter groups associated with the OIDC user")
        val groupFilter: String?
) {

    /**
     * Erases the [clientSecret].
     */
    fun obfuscate() = OntrackOIDCProvider(
            id, name, description, issuerId, clientId, "", groupFilter
    )

}