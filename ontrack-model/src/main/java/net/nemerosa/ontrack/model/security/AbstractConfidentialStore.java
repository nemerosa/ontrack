package net.nemerosa.ontrack.model.security;

import net.nemerosa.ontrack.model.security.ConfidentialStore;

import java.security.SecureRandom;

/**
 * Provides random bytes.
 */
public abstract class AbstractConfidentialStore implements ConfidentialStore {

    private final SecureRandom sr = new SecureRandom();

    @Override
    public byte[] randomBytes(int size) {
        byte[] random = new byte[size];
        sr.nextBytes(random);
        return random;
    }

}
