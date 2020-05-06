package net.nemerosa.ontrack.extension.oidc.settings

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.structure.NameDescription

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
     * Cache of provider names (unsecured)
     */
    val cachedProviderNames: List<NameDescription>

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

    /**
     * Adds an [OIDCSettingsListener] to this service.
     */
    fun addOidcSettingsListener(listener: OIDCSettingsListener)

    /**
     * Gets any image associated with this provider
     *
     * @param id [ID][OntrackOIDCProvider.id] of the provider
     * @return Image or `null` if no image is associated
     */
    fun getProviderImage(id: String): Document?

    /**
     * Checks if any image associated with this provider
     *
     * @param id [ID][OntrackOIDCProvider.id] of the provider
     * @return Flag indicating if an image is associated with the provider
     */
    fun hasProviderImage(id: String): Boolean

    /**
     * Sets (or unsets) the image associated with a provider
     *
     * @param id [ID][OntrackOIDCProvider.id] of the provider
     * @param image Image or `null` to unset
     */
    fun setProviderImage(id: String, image: Document?)


}