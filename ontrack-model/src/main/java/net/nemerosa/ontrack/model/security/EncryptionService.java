package net.nemerosa.ontrack.model.security;

/**
 * Service to decrypt and encrypt secrets.
 */
public interface EncryptionService {

    /**
     * Encrypts a secret, given in its plain form, and returns a string suitable for secure storage.
     *
     * @param plain Secret to encode
     * @return Encrypted secret
     */
    String encrypt(String plain);

    /**
     * Decrypts a secret into a plain form from its encrypted version.
     *
     * @param crypted Encrypted secret
     * @return Plain secret
     */
    String decrypt(String crypted);

    /**
     * Gets the encryption key.
     *
     * @return Key encoded as Base64
     * @throws EncryptionException If the key cannot be exported
     */
    String exportKey();

    /**
     * Imports the encryption key.
     *
     * @param key Key encoded as Base64
     * @throws EncryptionException If the key cannot be imported
     */
    void importKey(String key);
}
