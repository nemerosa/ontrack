package net.nemerosa.ontrack.extension.vault;

import net.nemerosa.ontrack.model.support.OntrackConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;

import java.net.URI;

import static net.nemerosa.ontrack.extension.vault.VaultExtensionFeature.VAULT_KEY_STORE_PROPERTY;

@Configuration
@ConditionalOnProperty(name = OntrackConfigProperties.KEY_STORE, havingValue = VAULT_KEY_STORE_PROPERTY)
public class VaultConfig extends AbstractVaultConfiguration {

    private final VaultConfigProperties configProperties;

    @Autowired
    public VaultConfig(VaultConfigProperties configProperties) {
        this.configProperties = configProperties;
    }

    @Override
    public VaultEndpoint vaultEndpoint() {
        return VaultEndpoint.from(URI.create(configProperties.getUri()));
    }

    @Override
    public ClientAuthentication clientAuthentication() {
        return new TokenAuthentication(
                configProperties.getToken()
        );
    }
}
