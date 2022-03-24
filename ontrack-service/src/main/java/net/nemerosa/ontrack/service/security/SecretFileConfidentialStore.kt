package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.AbstractConfidentialStore
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.io.File

/**
 * Reads the secret key directly from a file, never writes them.
 *
 * This store is suitable for read-only secrets (stored in K8S secrets
 * mounted as volumes for example).
 */
@Component
@ConditionalOnProperty(name = [OntrackConfigProperties.KEY_STORE], havingValue = "secret", matchIfMissing = true)
class SecretFileConfidentialStore(
    ontrackConfigProperties: OntrackConfigProperties,
) : AbstractConfidentialStore() {

    private val key: ByteArray

    init {
        val path = ontrackConfigProperties.fileKeyStore.directory
        LoggerFactory.getLogger(SecretFileConfidentialStore::class.java).info(
            "[key-store] Using Secret based key store (directory = $path)"
        )
        if (path.isBlank()) {
            throw SecretFileConfidentialStoreNoDirectoryException()
        } else {
            val directory = File(path)
            if (!directory.exists() || !directory.isDirectory) {
                throw SecretFileConfidentialStoreInvalidDirectoryException(path)
            } else {
                val keyFile = File(directory, EncryptionServiceKeys.ENCRYPTION_KEY)
                if (!keyFile.exists() || !keyFile.isFile || !keyFile.canRead()) {
                    throw SecretFileConfidentialStoreMissingKeyFileException(keyFile)
                } else {
                    key = keyFile.readBytes()
                }
            }
        }
    }

    override fun store(key: String, payload: ByteArray) {
        throw SecretFileConfidentialStoreReadOnlyException(key)
    }

    override fun load(key: String): ByteArray? =
        if (key == EncryptionServiceKeys.ENCRYPTION_KEY) {
            this.key
        } else {
            null
        }
}