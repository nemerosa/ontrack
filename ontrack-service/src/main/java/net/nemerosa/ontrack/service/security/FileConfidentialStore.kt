package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.common.Utils
import net.nemerosa.ontrack.model.security.AbstractConfidentialStore
import net.nemerosa.ontrack.model.support.EnvService
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.security.GeneralSecurityException
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.SecretKey

/**
 * Storing the keys as files in a directory.
 *
 * @property rootDir Directory that stores individual keys.
 */
@Component
@ConditionalOnProperty(name = [OntrackConfigProperties.KEY_STORE], havingValue = "file", matchIfMissing = true)
class FileConfidentialStore(
        private val rootDir: File
) : AbstractConfidentialStore() {

    /**
     * The master key.
     *
     *
     * The sole purpose of the master key is to encrypt individual keys on the disk.
     * Because leaking this master key compromises all the individual keys, we must not let
     * this master key used for any other purpose, hence the protected access.
     */
    private val masterKey: SecretKey

    @Autowired
    constructor(envService: EnvService) : this(envService.getWorkingDir("security", "secrets"))

    /**
     * Persists the payload of a key to the disk.
     */
    override fun store(key: String, payload: ByteArray) {
        try {
            val sym = Cipher.getInstance("AES")
            sym.init(Cipher.ENCRYPT_MODE, masterKey)
            FileOutputStream(getFileFor(key)).use { fos ->
                CipherOutputStream(fos, sym).use { cos ->
                    cos.write(payload)
                    cos.write(MAGIC)
                }
            }
        } catch (e: GeneralSecurityException) {
            throw IOException("Failed to persist the key: $key", e)
        }
    }

    /**
     * Reverse operation of [store]
     *
     * @return null the data has not been previously persisted.
     */
    override fun load(key: String): ByteArray? {
        try {
            val f = getFileFor(key)
            if (!f.exists()) return null
            val sym = Cipher.getInstance("AES")
            sym.init(Cipher.DECRYPT_MODE, masterKey)
            FileInputStream(f).use { fis ->
                CipherInputStream(fis, sym).use { cis ->
                    val bytes = IOUtils.toByteArray(cis)
                    return verifyMagic(bytes)
                }
            }
        } catch (e: GeneralSecurityException) {
            throw IOException("Failed to persist the key: $key", e)
        }
    }

    /**
     * Verifies that the given byte[] has the MAGIC trailer, to verify the integrity of the decryption process.
     */
    private fun verifyMagic(payload: ByteArray): ByteArray? {
        val payloadLen = payload.size - MAGIC.size
        if (payloadLen < 0) return null // obviously broken
        for (i in MAGIC.indices) {
            if (payload[payloadLen + i] != MAGIC[i]) return null // broken
        }
        val truncated = ByteArray(payloadLen)
        System.arraycopy(payload, 0, truncated, 0, truncated.size)
        return truncated
    }

    private fun getFileFor(key: String): File {
        return File(rootDir, key)
    }

    companion object {
        private const val ENCODING = "UTF-8"
        private val MAGIC = "::::MAGIC::::".toByteArray()
    }

    init {
        LoggerFactory.getLogger(FileConfidentialStore::class.java).info(
                "[key-store] Using file based key store at {}",
                rootDir.absolutePath
        )
        val masterSecret = File(rootDir, "master.key")
        if (!masterSecret.exists()) {
            // we are only going to use small number of bits (since export control limits AES key length)
            // but let's generate a long enough key anyway
            FileUtils.write(masterSecret, Utils.toHexString(randomBytes(128)), ENCODING)
        }
        masterKey = SecurityUtils.toAes128Key(FileUtils.readFileToString(masterSecret, ENCODING))
    }
}