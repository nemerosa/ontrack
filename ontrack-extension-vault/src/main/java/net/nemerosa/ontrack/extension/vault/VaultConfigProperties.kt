package net.nemerosa.ontrack.extension.vault

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "ontrack.config.vault")
class VaultConfigProperties {
    /**
     * URI to the Vault end point
     */
    var uri = "http://localhost:8200"
    /**
     * Token authentication
     */
    var token = "test"
    /**
     * Key prefix
     */
    var prefix = "ontrack/keys"

}