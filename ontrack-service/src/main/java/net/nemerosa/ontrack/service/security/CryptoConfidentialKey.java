package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.model.security.ConfidentialStore;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Base64;

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

    private SecretKey getKey() {
        try {
            if (secret == null) {
                synchronized (this) {
                    if (secret == null) {
                        byte[] payload = confidentialStore.load(id);
                        if (payload == null) {
                            payload = confidentialStore.randomBytes(256);
                            confidentialStore.store(id, payload);
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

    @Override
    public String encrypt(String plain) {
        try {
            // Creates a cipher
            Cipher cipher = encrypt();
            cipher.init(Cipher.ENCRYPT_MODE, getKey());
            // Message as bytes
            byte[] bytes = plain.getBytes("UTF-8");
            // Encryption
            byte[] encryptedBytes = cipher.doFinal(bytes);
            // Base64 encoding
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (GeneralSecurityException | IOException ex) {
            throw new EncryptionException(ex);
        }
    }

    @Override
    public String decrypt(String crypted) {
        try {
            // Creates a cipher
            Cipher cipher = decrypt();
            cipher.init(Cipher.DECRYPT_MODE, getKey());
            // Decodes from Base64
            byte[] encryptedBytes = Base64.getDecoder().decode(crypted);
            // Decrypts
            byte[] bytes = cipher.doFinal(encryptedBytes);
            // As UTF-8 string
            return new String(bytes, "UTF-8");
        } catch (GeneralSecurityException | IOException ex) {
            throw new EncryptionException(ex);
        }
    }

    /**
     * Returns a {@link javax.crypto.Cipher} object for encrypting with this key.
     */
    @Override
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
    @Override
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
