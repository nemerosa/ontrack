package net.nemerosa.ontrack.extension.oidc.settings

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