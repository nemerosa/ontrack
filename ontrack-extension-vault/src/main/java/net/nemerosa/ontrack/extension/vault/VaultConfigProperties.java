package net.nemerosa.ontrack.extension.vault;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "ontrack.config.vault")
public class VaultConfigProperties {

    /**
     * URI to the Vault end point
     */
    private String uri = "http://localhost:8200";

    /**
     * Token authentication
     */
    private String token = "test";

    /**
     * Key prefix
     */
    private String prefix = "ontrack/secrets/key";

}
