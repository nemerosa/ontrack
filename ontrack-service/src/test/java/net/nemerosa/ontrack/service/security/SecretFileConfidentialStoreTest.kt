package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.junit.jupiter.api.Test
import java.security.SecureRandom
import kotlin.io.path.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class SecretFileConfidentialStoreTest {

    @Test
    fun `Invalid if directory property is not set`() {
        val properties = OntrackConfigProperties()
        assertFailsWith<SecretFileConfidentialStoreNoDirectoryException> {
            SecretFileConfidentialStore(properties)
        }
    }

    @Test
    fun `Invalid if directory does not exist`() {
        val file = createTempDirectory()
        file.deleteExisting()
        val properties = OntrackConfigProperties().apply {
            fileKeyStore.directory = file.absolutePathString()
        }
        assertFailsWith<SecretFileConfidentialStoreInvalidDirectoryException> {
            SecretFileConfidentialStore(properties)
        }
    }

    @Test
    fun `Invalid if directory is not a directory`() {
        val file = kotlin.io.path.createTempFile()
        val properties = OntrackConfigProperties().apply {
            fileKeyStore.directory = file.absolutePathString()
        }
        assertFailsWith<SecretFileConfidentialStoreInvalidDirectoryException> {
            SecretFileConfidentialStore(properties)
        }
    }

    @Test
    fun `Invalid if key file does not exist`() {
        val root = createTempDirectory()
        val properties = OntrackConfigProperties().apply {
            fileKeyStore.directory = root.absolutePathString()
        }
        assertFailsWith<SecretFileConfidentialStoreMissingKeyFileException> {
            SecretFileConfidentialStore(properties)
        }
    }

    @Test
    fun `Invalid if key file is not a file`() {
        val root = createTempDirectory()
        root.resolve(EncryptionServiceKeys.ENCRYPTION_KEY).createDirectory()
        val properties = OntrackConfigProperties().apply {
            fileKeyStore.directory = root.absolutePathString()
        }
        assertFailsWith<SecretFileConfidentialStoreMissingKeyFileException> {
            SecretFileConfidentialStore(properties)
        }
    }

    @Test
    fun `Valid key file`() {

        val sr = SecureRandom()
        val random = ByteArray(256)
        sr.nextBytes(random)

        val root = createTempDirectory()
        val keyFile = root.resolve(EncryptionServiceKeys.ENCRYPTION_KEY).createFile()
        keyFile.writeBytes(random)

        val properties = OntrackConfigProperties().apply {
            fileKeyStore.directory = root.absolutePathString()
        }
        val store = SecretFileConfidentialStore(properties)

        val encryptionService = EncryptionServiceImpl(store)
        val plain = "some plain text"
        val encrypted = encryptionService.encrypt(plain)
        assertNotNull(encrypted) {
            assertTrue(it != plain, "Encrypted")
        }
        val decrypted = encryptionService.decrypt(encrypted)
        assertEquals(plain, decrypted, "Decrypted")
    }

}