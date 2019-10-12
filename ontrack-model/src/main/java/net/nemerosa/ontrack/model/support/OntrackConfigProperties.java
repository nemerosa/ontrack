package net.nemerosa.ontrack.model.support;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.validation.constraints.Min;

/**
 * Configuration properties for Ontrack.
 */
@Data
@Component
@ConfigurationProperties(prefix = "ontrack.config")
@Validated
public class OntrackConfigProperties {

    /**
     * Key store type
     */
    public static final String KEY_STORE = "ontrack.config.key-store";

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
    @Min(1)
    private int applicationLogInfoMax = 10;

    /**
     * Home directory
     */
    private String applicationWorkingDir = "work/files";

    /**
     * Testing the configurations of external configurations
     */
    private boolean configurationTest = true;

    /**
     * Job configuration
     */
    @Valid
    private JobConfigProperties jobs = new JobConfigProperties();

    /**
     * Label provider collection job activation
     */
    private boolean jobLabelProviderEnabled = false;

    @PostConstruct
    public void log() {
        if (!configurationTest) {
            logger.warn("[config] Tests of external configurations are disabled");
        }
    }

}
