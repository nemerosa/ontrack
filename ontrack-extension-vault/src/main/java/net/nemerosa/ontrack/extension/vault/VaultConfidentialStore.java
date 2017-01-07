package net.nemerosa.ontrack.extension.vault;

import net.nemerosa.ontrack.model.security.AbstractConfidentialStore;
import net.nemerosa.ontrack.model.support.OntrackConfigProperties;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.vault.core.VaultOperations;
import org.springframework.vault.support.VaultResponseSupport;

import java.io.IOException;

import static net.nemerosa.ontrack.extension.vault.VaultExtensionFeature.VAULT_KEY_STORE_PROPERTY;

@Component
@ConditionalOnProperty(name = OntrackConfigProperties.KEY_STORE, havingValue = VAULT_KEY_STORE_PROPERTY)
public class VaultConfidentialStore extends AbstractConfidentialStore {

    private final VaultOperations vaultOperations;

    @Autowired
    public VaultConfidentialStore(VaultOperations vaultOperations) {
        this.vaultOperations = vaultOperations;
        LoggerFactory.getLogger(VaultConfidentialStore.class).info(
                "[key-store] Using Vault store"
        );
    }

    protected String getPath(String key) {
        return String.format("ontrack/secrets/key/%s", key);
    }

    @Override
    public void store(String key, byte[] payload) throws IOException {
        vaultOperations.write(
                getPath(key),
                payload
        );
    }

    @Override
    public byte[] load(String key) throws IOException {
        VaultResponseSupport<byte[]> support = vaultOperations.read(
                getPath(key),
                byte[].class
        );
        return support != null ? support.getData() : null;
    }

}
