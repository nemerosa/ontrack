package net.nemerosa.ontrack.extension.oidc.settings

/**
 * Management of OIDC providers.
 */
interface OIDCSettingsService {

    /**
     * Gets the list of providers
     */
    val providers: List<OntrackOIDCProvider>

    /**
     * Cache of providers
     */
    val cachedProviders: List<OntrackOIDCProvider>

    /**
     * Creation of a OIDC provider
     */
    fun createProvider(input: OntrackOIDCProvider): OntrackOIDCProvider

}