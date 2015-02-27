package net.nemerosa.ontrack.model.support;

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

    /**
     * Home directory
     */
    private String applicationWorkingDir = "work/files";

    /**
     * Graphite host for metrics, disabled by default
     */
    private String graphiteHost = null;

    /**
     * Graphite port for metrics
     */
    private int graphitePort = 2003;

}
