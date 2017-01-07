package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.EncryptionService
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired

class EncryptionServiceIT extends AbstractServiceTestSupport {

    @Autowired
    private EncryptionService encryptionService

    @Test
    void 'Encryption and decryption'() {
        // Encrypts a secret
        def encrypted = encryptionService.encrypt('verysecret')
        assert encrypted != null
        assert encrypted.length() > 0
        // Decryption
        def decrypted = encryptionService.decrypt(encrypted)
        assert decrypted == 'verysecret'
    }

}
