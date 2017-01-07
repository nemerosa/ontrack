package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.EncryptionService
import org.apache.commons.io.FileUtils
import org.junit.Test

class EncryptionServiceTest {

    @Test
    void 'Encryption and decryption'() {
        // Temporary directory
        File dir = File.createTempDir()
        try {
            // Encryption service
            EncryptionService encryptionService = new EncryptionServiceImpl(
                    new FileConfidentialStore(
                            dir
                    )
            )
            // Asserts master key file is created
            assert new File(dir, 'master.key').exists()
            // Encrypts a secret
            def encrypted = encryptionService.encrypt('verysecret')
            assert encrypted != null
            assert encrypted.length() > 0
            // Asserts key file is created
            assert new File(dir, 'net.nemerosa.ontrack.security.EncryptionServiceImpl.encryption').exists()
            // Decryption
            def decrypted = encryptionService.decrypt(encrypted)
            assert decrypted == 'verysecret'
        } finally {
            FileUtils.deleteQuietly(dir)
        }
    }

}
