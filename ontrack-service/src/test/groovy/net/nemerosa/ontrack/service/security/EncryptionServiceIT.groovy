package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.it.AbstractServiceTestSupport
import net.nemerosa.ontrack.model.security.ApplicationManagement
import net.nemerosa.ontrack.model.security.EncryptionService
import net.nemerosa.ontrack.model.security.GlobalSettings
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException

import java.nio.charset.Charset

class EncryptionServiceIT extends AbstractServiceTestSupport {

    static final String KEY_PAYLOAD = Base64.encoder.encodeToString('test'.getBytes(Charset.forName('UTF-8')))

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

    @Test(expected = AccessDeniedException)
    void 'Export of the key denied to anonymous'() {
        asAnonymous().call { encryptionService.exportKey() }
    }

    @Test(expected = AccessDeniedException)
    void 'Import of the key denied to anonymous'() {
        asAnonymous().call { encryptionService.importKey(KEY_PAYLOAD) }
    }

    @Test(expected = AccessDeniedException)
    void 'Export of the key denied when only app mgt'() {
        asUser().with(ApplicationManagement).call { encryptionService.exportKey() }
    }

    @Test(expected = AccessDeniedException)
    void 'Import of the key denied when only app mgt'() {
        asUser().with(ApplicationManagement).call { encryptionService.importKey(KEY_PAYLOAD) }
    }

    @Test(expected = AccessDeniedException)
    void 'Export of the key denied when only global settings'() {
        asUser().with(GlobalSettings).call { encryptionService.exportKey() }
    }

    @Test(expected = AccessDeniedException)
    void 'Import of the key denied when only global settings'() {
        asUser().with(GlobalSettings).call { encryptionService.importKey(KEY_PAYLOAD) }
    }

    @Test
    void 'Import and export'() {
        asUser().with(GlobalSettings, ApplicationManagement).call {
            encryptionService.importKey(KEY_PAYLOAD)
            def encoded = encryptionService.exportKey()
            assert encoded == KEY_PAYLOAD
        }
    }

}
