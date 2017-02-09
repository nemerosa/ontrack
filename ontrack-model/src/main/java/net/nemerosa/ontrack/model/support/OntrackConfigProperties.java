package net.nemerosa.ontrack.model.support;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Configuration properties for Ontrack.
 */
@Data
@Component
@ConfigurationProperties(prefix = "ontrack.config")
public class OntrackConfigProperties {

    private final Logger logger = LoggerFactory.getLogger(OntrackConfigProperties.class);

    /**
     * Maximum number of days to keep the log entries
     */
    private int applicationLogRetentionDays = 7;

    /**
     * Number of fatal errors to notify into the GUI.
     *
     * @see ApplicationLogEntryLevel#FATAL
     */
    private int applicationLogInfoMax = 10;

    /**
     * Home directory
     */
    private String applicationWorkingDir = "work/files";

    /**
     * Metrics refresh period (in seconds)
     */
    private int metricsPeriod = 60;

    /**
     * Testing the configurations of external configurations
     */
    private boolean configurationTest = true;

    /**
     * Job configuration
     */
    private JobConfigProperties jobs = new JobConfigProperties();

    @PostConstruct
    public void log() {
        if (!configurationTest) {
            logger.warn("[config] Tests of external configurations are disabled");
        }
    }

}
