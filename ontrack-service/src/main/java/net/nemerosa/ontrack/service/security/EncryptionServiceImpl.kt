package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.io.IOException

/**
 * Default encryption service
 */
@Component
class EncryptionServiceImpl(private val key: ConfidentialKey) : EncryptionService {

    @Autowired
    @Suppress("SpringJavaInjectionPointsAutowiringInspection")
    constructor(confidentialStore: ConfidentialStore) : this(
            CryptoConfidentialKey(
                    confidentialStore,
                    "net.nemerosa.ontrack.security.EncryptionServiceImpl.encryption"
            )
    )

    override fun encrypt(plain: String?): String? = plain?.let { key.encrypt(it) }

    override fun decrypt(crypted: String?): String? = crypted?.let { key.decrypt(it) }

    override fun exportKey(): String? {
        checkAdmin()
        return try {
            key.exportKey()
        } catch (e: IOException) {
            throw EncryptionException(e)
        }
    }

    override fun importKey(key: String) {
        checkAdmin()
        try {
            this.key.importKey(key)
        } catch (e: IOException) {
            throw EncryptionException(e)
        }
    }

    private fun checkAdmin() {
        val authorised: Boolean
        val context = SecurityContextHolder.getContext()
        val authentication = context.authentication
        authorised = if (authentication != null && authentication.isAuthenticated && authentication.principal is OntrackAuthenticatedUser) {
            val user = authentication.principal as OntrackAuthenticatedUser
            user.isGranted(ApplicationManagement::class.java) &&
                    user.isGranted(GlobalSettings::class.java)
        } else {
            false
        }
        // NOT GRANTED
        if (!authorised) {
            throw AccessDeniedException(
                    "The current used has attempted to import/export keys without being authorised: " +
                            if (authentication != null) authentication.name else "anonymous"
            )
        }
    }

}