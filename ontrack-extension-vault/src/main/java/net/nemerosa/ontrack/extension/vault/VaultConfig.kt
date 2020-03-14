package net.nemerosa.ontrack.extension.vault

import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.vault.authentication.ClientAuthentication
import org.springframework.vault.authentication.TokenAuthentication
import org.springframework.vault.client.VaultEndpoint
import org.springframework.vault.config.AbstractVaultConfiguration
import java.net.URI

@Configuration
@ConditionalOnProperty(name = [OntrackConfigProperties.KEY_STORE], havingValue = VaultExtensionFeature.VAULT_KEY_STORE_PROPERTY)
class VaultConfig(
        private val configProperties: VaultConfigProperties
) : AbstractVaultConfiguration() {

    override fun vaultEndpoint(): VaultEndpoint = VaultEndpoint.from(URI.create(configProperties.uri))

    override fun clientAuthentication(): ClientAuthentication = TokenAuthentication(
            configProperties.token
    )

}