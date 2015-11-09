package net.nemerosa.ontrack.extension.metrics.graphite;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 */
@Data
@Component
@ConfigurationProperties(prefix = GraphiteMetricsConfigProperties.PREFIX)
public class GraphiteMetricsConfigProperties {

    /**
     * Property prefix
     */
    public static final String PREFIX = "ontrack.metrics.graphite";

    /**
     * Criteria (host) prefix
     */
    public static final String HOST_PROPERTY = "ontrack.metrics.graphite.host";

    /**
     * Host
     */
    private String host;

    /**
     * Port
     */
    private int port = 2003;

    /**
     * Metrics refresh period (in seconds)
     */
    private int period = 60;

}
