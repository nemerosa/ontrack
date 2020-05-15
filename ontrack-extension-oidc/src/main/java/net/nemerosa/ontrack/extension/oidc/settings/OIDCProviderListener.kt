package net.nemerosa.ontrack.extension.oidc.settings

/**
 * Listens to events on [OntrackOIDCProvider].
 */
interface OIDCProviderListener {

    /**
     * Called when the [provider] is about to be deleted.
     */
    fun onOIDCProviderDeleted(provider: OntrackOIDCProvider)

}