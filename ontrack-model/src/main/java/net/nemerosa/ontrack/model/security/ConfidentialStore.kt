package net.nemerosa.ontrack.model.security;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public interface ConfidentialStore {
    /**
     * Persists the payload of key to a persisted storage (such as disk.)
     * The expectation is that the persisted form is secure.
     */
    void store(String key, byte[] payload) throws IOException;

    /**
     * Reverse operation of {@link #store(String, byte[])}
     *
     * @return null the data has not been previously persisted, or if the data was tampered.
     */
    @Nullable
    byte[] load(String key) throws IOException;

    /**
     * Works like {@link java.security.SecureRandom#nextBytes(byte[])}.
     * <p>
     * This enables implementations to consult other entropy sources, if it's available.
     */
    byte[] randomBytes(int size);
}
