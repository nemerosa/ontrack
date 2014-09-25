package net.nemerosa.ontrack.model.settings;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.security.KeyStore;

/**
 * Configuration properties for Ontrack.
 */
@Data
@Component
@ConfigurationProperties(prefix = "ontrack.config")
public class OntrackConfigProperties {

    /**
     * Maximum number of application messages to retain
     */
    private int applicationLogMaxEntries = 1000;

    /**
     * Home directory
     */
    private String applicationWorkingDir = "work/files";

    /**
     * Keystore type
     */
    private String cryptoKeyStoreType = KeyStore.getDefaultType();

    /**
     * Key store password
     */
    private String cryptoKeyStorePassword = "ontrack";

    /**
     * Key alias
     */
    private String cryptoKeyAlias = "ontrack";

    /**
     * Cipher type
     */
    private String cryptoCipherType = "DES/ECB/PKCS5Padding";

}
