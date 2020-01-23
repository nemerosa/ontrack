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
@ConfigurationProperties(prefix = OntrackConfigProperties.PREFIX)
@Validated
public class OntrackConfigProperties {

    /**
     * Properties prefix
     */
    public static final String PREFIX = "ontrack.config";

    /**
     * Search service key
     */
    public static final String SEARCH = "search";

    /**
     * Search complete key
     */
    public static final String SEARCH_PROPERTY = PREFIX + "." + SEARCH;

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

    /**
     * Search engine to be used.
     */
    private String search = SEARCH;

    @PostConstruct
    public void log() {
        if (!configurationTest) {
            logger.warn("[config] Tests of external configurations are disabled");
        }
    }

}
