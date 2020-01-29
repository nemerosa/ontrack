package net.nemerosa.ontrack.model.support

import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated
import javax.annotation.PostConstruct
import javax.validation.Valid
import javax.validation.constraints.Min

/**
 * Configuration properties for Ontrack.
 */
@Component
@ConfigurationProperties(prefix = OntrackConfigProperties.PREFIX)
@Validated
class OntrackConfigProperties {

    private var logger = LoggerFactory.getLogger(OntrackConfigProperties::class.java)

    /**
     * Maximum number of days to keep the log entries
     */
    private var applicationLogRetentionDays = 7
    /**
     * Number of fatal errors to notify into the GUI.
     *
     * @see ApplicationLogEntryLevel.FATAL
     */
    @Min(1)
    private var applicationLogInfoMax = 10
    /**
     * Home directory
     */
    private var applicationWorkingDir = "work/files"
    /**
     * Testing the configurations of external configurations
     */
    private var configurationTest = true
    /**
     * Job configuration
     */
    @Valid
    private var jobs: JobConfigProperties = JobConfigProperties()
    /**
     * Label provider collection job activation
     */
    private var jobLabelProviderEnabled = false
    /**
     * Search engine to be used.
     */
    private var search = SEARCH
    /**
     * Flag to enable immediate re-indexation after items are added into the search index (used mostly
     * for testing).
     */
    private var searchIndexImmediate = false

    @PostConstruct
    fun log() {
        if (!configurationTest) {
            logger.warn("[config] Tests of external configurations are disabled")
        }
    }

    companion object {
        /**
         * Properties prefix
         */
        const val PREFIX = "ontrack.config"
        /**
         * Search service key
         */
        const val SEARCH = "search"
        /**
         * Search complete key
         */
        const val SEARCH_PROPERTY = "$PREFIX.$SEARCH"
        /**
         * Key store type
         */
        const val KEY_STORE = "ontrack.config.key-store"
    }
}