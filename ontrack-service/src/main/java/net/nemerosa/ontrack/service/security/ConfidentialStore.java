package net.nemerosa.ontrack.service.security;

import java.io.IOException;

public interface ConfidentialStore {
    /**
     * Persists the payload of {@link ConfidentialKey} to a persisted storage (such as disk.)
     * The expectation is that the persisted form is secure.
     */
    void store(ConfidentialKey key, byte[] payload) throws IOException;

    /**
     * Reverse operation of {@link #store(ConfidentialKey, byte[])}
     *
     * @return null the data has not been previously persisted, or if the data was tampered.
     */
    byte[] load(ConfidentialKey key) throws IOException;

    /**
     * Works like {@link java.security.SecureRandom#nextBytes(byte[])}.
     * <p>
     * This enables implementations to consult other entropy sources, if it's available.
     */
    byte[] randomBytes(int size);

    /**
     * Gets the unique ID for this store, which allows for its selection in
     * {@link net.nemerosa.ontrack.model.support.OntrackConfigProperties}.
     *
     * @return A unique ID
     * @see
     */
    String getId();
}
