package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.ConfidentialStore
import net.nemerosa.ontrack.model.security.EncryptionException
import java.io.IOException
import java.security.GeneralSecurityException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class CryptoConfidentialKey(private val confidentialStore: ConfidentialStore, override val id: String) : ConfidentialKey {

    @Volatile
    private var secret: SecretKey? = null

    override fun exportKey(): String? {
        val payload = confidentialStore.load(id)
        return if (payload != null) {
            Base64.getEncoder().encodeToString(payload)
        } else {
            null
        }
    }

    override fun importKey(key: String) {
        confidentialStore.store(
                id,
                Base64.getDecoder().decode(key)
        )
        // Reimporting the secret key
        secret = null
    }

    // Due to the stupid US export restriction JDK only ships 128bit version.
    private val key: SecretKey?
        get() = try {
            if (secret == null) {
                synchronized(this) {
                    if (secret == null) {
                        var payload = confidentialStore.load(id)
                        if (payload == null) {
                            payload = confidentialStore.randomBytes(256)
                            confidentialStore.store(id, payload)
                        }
                        // Due to the stupid US export restriction JDK only ships 128bit version.
                        secret = SecretKeySpec(payload, 0, 128 / 8, ALGORITHM)
                    }
                }
            }
            secret
        } catch (e: IOException) {
            throw Error("Failed to load the key: $id", e)
        }

    override fun encrypt(plain: String): String {
        return try {
            // Creates a cipher
            val cipher = encrypt()
            cipher.init(Cipher.ENCRYPT_MODE, key)
            // Message as bytes
            val bytes = plain.toByteArray(charset("UTF-8"))
            // Encryption
            val encryptedBytes = cipher.doFinal(bytes)
            // Base64 encoding
            Base64.getEncoder().encodeToString(encryptedBytes)
        } catch (ex: GeneralSecurityException) {
            throw EncryptionException(ex)
        } catch (ex: IOException) {
            throw EncryptionException(ex)
        }
    }

    override fun decrypt(crypted: String): String {
        return try {
            // Creates a cipher
            val cipher = decrypt()
            cipher.init(Cipher.DECRYPT_MODE, key)
            // Decodes from Base64
            val encryptedBytes = Base64.getDecoder().decode(crypted)
            // Decrypts
            val bytes = cipher.doFinal(encryptedBytes)
            // As UTF-8 string
            String(bytes, Charsets.UTF_8)
        } catch (ex: GeneralSecurityException) {
            throw EncryptionException(ex)
        } catch (ex: IOException) {
            throw EncryptionException(ex)
        }
    }

    /**
     * Returns a [javax.crypto.Cipher] object for encrypting with this key.
     */
    override fun encrypt(): Cipher {
        return try {
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, key)
            cipher
        } catch (e: GeneralSecurityException) {
            throw AssertionError(e)
        }
    }

    /**
     * Returns a [javax.crypto.Cipher] object for decrypting with this key.
     */
    override fun decrypt(): Cipher {
        return try {
            val cipher = Cipher.getInstance(ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, key)
            cipher
        } catch (e: GeneralSecurityException) {
            throw AssertionError(e)
        }
    }

    companion object {
        private const val ALGORITHM = "AES"
    }

}