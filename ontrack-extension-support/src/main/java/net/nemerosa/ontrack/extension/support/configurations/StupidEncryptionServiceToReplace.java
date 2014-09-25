package net.nemerosa.ontrack.extension.support.configurations;

import net.nemerosa.ontrack.security.ConfidentialKey;
import net.nemerosa.ontrack.security.ConfidentialStore;
import net.nemerosa.ontrack.security.CryptoConfidentialKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * FIXME #77 No encryption is provided for the moment by ontrack.
 * <p>
 * Replace this dummy implementation by a real one.
 * <p>
 * This class has probably to be moved to the <code>ontrack-service</code> module.
 */
@Component
public class StupidEncryptionServiceToReplace implements EncryptionService {

    private final ConfidentialKey key;

    @Autowired
    public StupidEncryptionServiceToReplace(ConfidentialStore confidentialStore) {
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
