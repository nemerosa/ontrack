package net.nemerosa.ontrack.model.security;

public interface EncryptionService {
    String encrypt(String plain);

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
