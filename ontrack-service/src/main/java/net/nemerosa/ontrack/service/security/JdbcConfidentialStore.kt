package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.security.AbstractConfidentialStore
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import net.nemerosa.ontrack.model.support.StorageService
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * Storing the keys in the database.
 *
 *
 * This is easy to setup but is NOT secure.
 */
@Component
@ConditionalOnProperty(name = [OntrackConfigProperties.KEY_STORE], havingValue = "jdbc")
class JdbcConfidentialStore(
        private val storageService: StorageService
) : AbstractConfidentialStore() {

    override fun store(key: String, payload: ByteArray) {
        storageService.store(
                JdbcConfidentialStore::class.java.simpleName,
                key,
                Key(payload)
        )
    }

    override fun load(key: String): ByteArray? {
        return storageService.retrieve(
                JdbcConfidentialStore::class.java.simpleName,
                key,
                Key::class.java
        )
                .map { obj: Key -> obj.payload }
                .orElse(null)
    }

    class Key(
            val payload: ByteArray
    )

    init {
        LoggerFactory.getLogger(JdbcConfidentialStore::class.java).info(
                "[key-store] Using JDBC based key store"
        )
    }
}