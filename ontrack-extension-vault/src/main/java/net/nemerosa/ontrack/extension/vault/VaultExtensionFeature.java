package net.nemerosa.ontrack.extension.vault;

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature;
import org.springframework.stereotype.Component;

@Component
public class VaultExtensionFeature extends AbstractExtensionFeature {

    public static final String VAULT_KEY_STORE_PROPERTY = "vault";

    public VaultExtensionFeature() {
        super(
                "id",
                "Vault",
                "Storage of keys in Vault"
        );
    }
}
