package net.nemerosa.ontrack.extension.vault

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseOrNull
import net.nemerosa.ontrack.model.security.AbstractConfidentialStore
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.apache.commons.lang3.Validate
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import org.springframework.vault.core.VaultKeyValueOperationsSupport
import org.springframework.vault.core.VaultOperations
import org.springframework.vault.core.get

@Component
@ConditionalOnProperty(name = [OntrackConfigProperties.KEY_STORE], havingValue = VaultExtensionFeature.VAULT_KEY_STORE_PROPERTY)
class VaultConfidentialStore(
        private val vaultOperations: VaultOperations,
        private val configProperties: VaultConfigProperties
) : AbstractConfidentialStore() {

    private fun kvOps() = vaultOperations.opsForKeyValue("/secret/data", VaultKeyValueOperationsSupport.KeyValueBackend.unversioned())

    override fun store(key: String, payload: ByteArray) {
        Validate.notNull(payload, "Key payload must not be null")
        kvOps().put("${configProperties.prefix}/$key", VaultPayload(Key(payload).asJson()))
    }

    override fun load(key: String): ByteArray? {
        val payload = kvOps().get<VaultPayload>("${configProperties.prefix}/$key")
        return payload?.data?.data?.parseOrNull<Key>()?.payload
    }

    init {
        LoggerFactory.getLogger(VaultConfidentialStore::class.java).info(
                "[key-store] Using Vault store at {}",
                configProperties.uri
        )
    }
}