package net.nemerosa.ontrack.extension.oidc.settings

import net.nemerosa.ontrack.model.Ack

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

    /**
     * Deletion of an OIDC provider
     *
     * @param id [ID][OntrackOIDCProvider.id] of the provider to delete
     */
    fun deleteProvider(id: String): Ack

    /**
     * Gets a provider using its [ID][OntrackOIDCProvider.id]
     *
     * @param id [ID][OntrackOIDCProvider.id] of the provider to delete
     * @return Associated provider or `null` if not found
     */
    fun getProviderById(id: String): OntrackOIDCProvider?

    /**
     * Updates a provider.
     *
     * @param input Provider to update
     * @return Updated provider
     */
    fun updateProvider(input: OntrackOIDCProvider): OntrackOIDCProvider

}