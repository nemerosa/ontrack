package net.nemerosa.ontrack.extension.metrics.influxdb;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 */
@Data
@Component
@ConfigurationProperties(prefix = InfluxdbMetricsConfigProperties.PREFIX)
public class InfluxdbMetricsConfigProperties {

    /**
     * Property prefix
     */
    public static final String PREFIX = "ontrack.metrics.influxdb";

    /**
     * Criteria (host) prefix
     */
    public static final String HOST_PROPERTY = "ontrack.metrics.influxdb.host";

    /**
     * Host
     */
    private String host;

    /**
     * Port
     */
    private int port = 8086;

    /**
     * User
     */
    private String user = "root";

    /**
     * Password
     */
    private String password = "root";

    /**
     * Database name
     */
    private String database = "ontrack";

    /**
     * Metrics refresh period (in seconds)
     */
    private int period = 60;

    /**
     * Retention policy. Use "default" for InfluxDB version < 1.0.0
     */
    private String retentionPolicy = "autogen";

}
