package net.nemerosa.ontrack.service.security;

/**
 * Getting access to the key store service.
 */
public interface ConfidentialStoreService {

    /**
     * Gets the confidential store to use.
     *
     * @return Returns the configured confidential store.
     */
    ConfidentialStore getConfidentialStore();

}
