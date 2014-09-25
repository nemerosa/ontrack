package net.nemerosa.ontrack.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class CryptoConfidentialKey implements ConfidentialKey {

    private final ConfidentialStore confidentialStore;
    private final String id;
    private volatile SecretKey secret;

    public CryptoConfidentialKey(ConfidentialStore confidentialStore, String id) {
        this.confidentialStore = confidentialStore;
        this.id = id;
    }

    @Override
    public String getId() {
        return id;
    }

    public CryptoConfidentialKey(ConfidentialStore confidentialStore, Class owner, String shortName) {
        this(confidentialStore, owner.getName() + '.' + shortName);
    }

    private SecretKey getKey() {
        try {
            if (secret == null) {
                synchronized (this) {
                    if (secret == null) {
                        byte[] payload = confidentialStore.load(this);
                        if (payload == null) {
                            payload = confidentialStore.randomBytes(256);
                            confidentialStore.store(this, payload);
                        }
                        // Due to the stupid US export restriction JDK only ships 128bit version.
                        secret = new SecretKeySpec(payload, 0, 128 / 8, ALGORITHM);
                    }
                }
            }
            return secret;
        } catch (IOException e) {
            throw new Error("Failed to load the key: " + getId(), e);
        }
    }

    /**
     * Returns a {@link javax.crypto.Cipher} object for encrypting with this key.
     */
    public Cipher encrypt() {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, getKey());
            return cipher;
        } catch (GeneralSecurityException e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Returns a {@link javax.crypto.Cipher} object for decrypting with this key.
     */
    public Cipher decrypt() {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getKey());
            return cipher;
        } catch (GeneralSecurityException e) {
            throw new AssertionError(e);
        }
    }


    private static final String ALGORITHM = "AES";
}
