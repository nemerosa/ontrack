package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.ApplicationManagement
import net.nemerosa.ontrack.model.security.EncryptionService
import net.nemerosa.ontrack.model.security.GlobalSettings
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException

import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class EncryptionServiceIT : AbstractServiceTestSupport() {

    private val keyPayload: String =
        Base64.getEncoder().encodeToString("test for a very secret key".toByteArray(Charsets.UTF_8))

    @Autowired
    private lateinit var encryptionService: EncryptionService

    @Test
    fun `Encryption and decryption`() {
        // Encrypts a secret
        val encrypted = encryptionService.encrypt("verysecret")
        assertNotNull(encrypted) {
            assertTrue(it.isNotEmpty())
        }
        // Decryption
        val decrypted = encryptionService.decrypt(encrypted)
        assertEquals("verysecret", decrypted)
    }

    @Test
    fun `Export of the key denied to anonymous`() {
        assertFailsWith<AccessDeniedException> {
            asAnonymous().call { encryptionService.exportKey() }
        }
    }

    @Test
    fun `Import of the key denied to anonymous`() {
        assertFailsWith<AccessDeniedException> {
            asAnonymous().call { encryptionService.importKey(keyPayload) }
        }
    }

    @Test
    fun `Export of the key denied when only app mgt`() {
        assertFailsWith<AccessDeniedException> {
            asUser().with(ApplicationManagement::class.java).call { encryptionService.exportKey() }
        }
    }

    @Test
    fun `Import of the key denied when only app mgt`() {
        assertFailsWith<AccessDeniedException> {
            asUser().with(ApplicationManagement::class.java).call { encryptionService.importKey(keyPayload) }
        }
    }

    @Test
    fun `Export of the key denied when only global settings`() {
        assertFailsWith<AccessDeniedException> {
            asUser().with(GlobalSettings::class.java).call { encryptionService.exportKey() }
        }
    }

    @Test
    fun `Import of the key denied when only global settings`() {
        assertFailsWith<AccessDeniedException> {
            asUser().with(GlobalSettings::class.java).call { encryptionService.importKey(keyPayload) }
        }
    }

    @Test
    fun `Import and export`() {
        asUser().with(GlobalSettings::class.java, ApplicationManagement::class.java).call {
            val old = encryptionService.exportKey()
            try {
                encryptionService.importKey(keyPayload)
                val encoded = encryptionService.exportKey()
                assertEquals(keyPayload, encoded)
            } finally {
                if (old != null) {
                    encryptionService.importKey(old)
                }
            }
        }
    }

}
