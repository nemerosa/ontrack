package net.nemerosa.ontrack.service.security

import io.mockk.mockk
import io.mockk.verify
import net.nemerosa.ontrack.model.security.SecurityService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class EncryptionServiceImplTest {

    private lateinit var service: EncryptionServiceImpl
    private lateinit var key: ConfidentialKey

    @BeforeEach
    fun before() {
        key = mockk<ConfidentialKey>(relaxed = true)
        val securityService = mockk<SecurityService>(relaxed = true)
        service = EncryptionServiceImpl(securityService, key)
    }

    @Test
    fun encrypt() {
        service.encrypt("test")
        verify(exactly = 1) {
            key.encrypt("test")
        }
    }

    @Test
    fun decrypt() {
        service.decrypt("xxxx")
        verify(exactly = 1) {
            key.decrypt("xxxx")
        }
    }

    @Test
    fun encrypt_null() {
        service.encrypt(null)
        verify(exactly = 0) {
            key.encrypt(any())
        }
    }

    @Test
    fun decrypt_null() {
        service.decrypt(null)
        verify(exactly = 0) {
            key.decrypt(any())
        }
    }
}