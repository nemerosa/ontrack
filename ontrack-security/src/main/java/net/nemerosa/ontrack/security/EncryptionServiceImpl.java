package net.nemerosa.ontrack.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Default encryption service
 */
@Component
public class EncryptionServiceImpl implements EncryptionService {

    private final ConfidentialKey key;

    @Autowired
    public EncryptionServiceImpl(ConfidentialStore confidentialStore) {
        // Creates or gets the key
        key = new CryptoConfidentialKey(confidentialStore, getClass(), "encryption");
    }

    @Override
    public String encrypt(String plain) {
        return key.encrypt(plain);
    }

    @Override
    public String decrypt(String crypted) {
        return key.decrypt(crypted);
    }
}
