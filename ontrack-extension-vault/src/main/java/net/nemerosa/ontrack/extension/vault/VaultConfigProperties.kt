package net.nemerosa.ontrack.extension.vault

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIName
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "ontrack.config.vault")
@APIName("Vault configuration")
@APIDescription("Ontrack can be configured to use Vault to store the encryption keys.")
class VaultConfigProperties {

    @APIDescription("URI to the Vault end point")
    var uri = "http://localhost:8200"

    @APIDescription("Token for the authentication")
    var token = "test"

    @APIDescription("Prefix to be used to store the keys")
    var prefix = "ontrack/keys"
}