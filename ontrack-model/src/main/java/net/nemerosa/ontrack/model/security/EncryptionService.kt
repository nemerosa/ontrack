package net.nemerosa.ontrack.model.security

/**
 * Service to decrypt and encrypt secrets.
 */
interface EncryptionService {
    /**
     * Encrypts a secret, given in its plain form, and returns a string suitable for secure storage.
     *
     * @param plain Secret to encode
     * @return Encrypted secret
     */
    fun encrypt(plain: String?): String?

    /**
     * Decrypts a secret into a plain form from its encrypted version.
     *
     * @param crypted Encrypted secret
     * @return Plain secret
     */
    fun decrypt(crypted: String?): String?

    /**
     * Gets the encryption key.
     *
     * @return Key encoded as Base64
     * @throws EncryptionException If the key cannot be exported
     */
    fun exportKey(): String?

    /**
     * Imports the encryption key.
     *
     * @param key Key encoded as Base64
     * @throws EncryptionException If the key cannot be imported
     */
    fun importKey(key: String)
}