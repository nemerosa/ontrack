package net.nemerosa.ontrack.extension.vault

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature
import org.springframework.stereotype.Component

@Component
class VaultExtensionFeature : AbstractExtensionFeature(
        "vault",
        "Vault",
        "Storage of keys in Vault"
) {

    companion object {
        const val VAULT_KEY_STORE_PROPERTY = "vault"
    }

}
