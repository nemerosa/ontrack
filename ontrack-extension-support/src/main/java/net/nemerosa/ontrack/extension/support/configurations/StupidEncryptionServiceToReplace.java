package net.nemerosa.ontrack.extension.support.configurations;

import net.nemerosa.ontrack.model.settings.OntrackConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.io.IOException;
import java.security.*;
import java.util.Base64;

/**
 * FIXME #77 No encryption is provided for the moment by ontrack.
 * <p>
 * Replace this dummy implementation by a real one.
 * <p>
 * This class has probably to be moved to the <code>ontrack-service</code> module.
 */
@Component
public class StupidEncryptionServiceToReplace implements EncryptionService {

    private final Key key;
    private final OntrackConfigProperties ontrackConfig;

    @Autowired
    public StupidEncryptionServiceToReplace(OntrackConfigProperties ontrackConfig) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
        this.ontrackConfig = ontrackConfig;
        // Gets the keystore
        KeyStore ks = KeyStore.getInstance(ontrackConfig.getCryptoKeyStoreType());
        // Checks if the key is defined
        if (!ks.containsAlias(ontrackConfig.getCryptoKeyAlias())) {
            // TODO Needs to create a key
        }
        // Gets the key
        key = ks.getKey(
                ontrackConfig.getCryptoKeyAlias(),
                ontrackConfig.getCryptoKeyStorePassword().toCharArray());
        // Done - deletes the password
        ontrackConfig.setCryptoKeyStorePassword(null);
    }

    @Override
    public String encrypt(String plain) {
        try {
            // Creates a cipher
            Cipher cipher = Cipher.getInstance(ontrackConfig.getCryptoCipherType());
            cipher.init(Cipher.ENCRYPT_MODE, key);
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
            Cipher cipher = Cipher.getInstance(ontrackConfig.getCryptoCipherType());
            cipher.init(Cipher.DECRYPT_MODE, key);
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
}
