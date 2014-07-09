package net.nemerosa.ontrack.service;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

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

}
