package net.nemerosa.ontrack.extension.oidc.settings

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIOptional
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
 * @property forceHttps Check to force the protocol to HTTPS for the Redirect URI
 * @property groupClaim Name of the access token claim that contains the list of groups. It defaults to `groups`.
 */
data class OntrackOIDCProvider(
    @get:NotNull(message = "The account name is required.")
    @get:Pattern(
        regexp = "[a-zA-Z0-9_-]+",
        message = "The ID must contain only letters, digits, underscores and dashes."
    )
    @APIDescription("Unique ID for this provider")
    val id: String,
    @APIDescription("Display name for this provider")
    val name: String,
    @APIDescription("Tooltip for this provider")
    @APIOptional
    val description: String,
    @APIDescription("OIDC issueId URL")
    val issuerId: String,
    @APIDescription("OIDC client ID")
    val clientId: String,
    @APIDescription("OIDC client secret")
    val clientSecret: String,
    @APIDescription("Regular expression used to filter groups associated with the OIDC user")
    val groupFilter: String?,
    @APIDescription("Check to force the protocol to HTTPS for the Redirect URI")
    @APIOptional
    val forceHttps: Boolean,
    @APIDescription("If true, this provider is disabled and won't be active")
    @APIOptional
    val disabled: Boolean,
    @APIDescription("Name of the access token claim that contains the list of groups. It defaults to `groups`.")
    @APIOptional
    val groupClaim: String? = null,
) {

    /**
     * Erases the [clientSecret].
     */
    fun obfuscate() = OntrackOIDCProvider(
        id, name, description, issuerId, clientId, "", groupFilter, forceHttps, disabled
    )

    /**
     * Disables this provider
     */
    fun disable() = withDisabled(true)

    /**
     * Disables flag for this provider
     */
    fun withDisabled(disabled: Boolean) = OntrackOIDCProvider(
        id = id,
        name = name,
        description = description,
        issuerId = issuerId,
        clientId = clientId,
        clientSecret = clientSecret,
        groupFilter = groupFilter,
        forceHttps = forceHttps,
        disabled = disabled,
    )

}