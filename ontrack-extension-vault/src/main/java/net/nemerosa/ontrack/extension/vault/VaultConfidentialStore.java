package net.nemerosa.ontrack.extension.vault;

import net.nemerosa.ontrack.model.security.AbstractConfidentialStore;
import net.nemerosa.ontrack.model.support.OntrackConfigProperties;
import org.apache.commons.lang3.Validate;
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
    private final VaultConfigProperties configProperties;

    @Autowired
    public VaultConfidentialStore(VaultOperations vaultOperations, VaultConfigProperties configProperties) {
        this.vaultOperations = vaultOperations;
        this.configProperties = configProperties;
        LoggerFactory.getLogger(VaultConfidentialStore.class).info(
                "[key-store] Using Vault store at {}",
                configProperties.getUri()
        );
    }

    protected String getPath(String key) {
        return String.format("%s/%s", configProperties.getPrefix(), key);
    }

    @Override
    public void store(String key, byte[] payload) throws IOException {
        Validate.notNull(payload, "Key payload must not be null");
        vaultOperations.write(
                getPath(key),
                new Key(payload)
        );
    }

    @Override
    public byte[] load(String key) throws IOException {
        VaultResponseSupport<Key> support = vaultOperations.read(
                getPath(key),
                Key.class
        );
        return support != null ? support.getData().getPayload() : null;
    }

}
