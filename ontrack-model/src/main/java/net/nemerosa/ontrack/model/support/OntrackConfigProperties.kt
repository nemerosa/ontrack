package net.nemerosa.ontrack.model.support

import org.slf4j.Logger
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

    private val logger: Logger = LoggerFactory.getLogger(OntrackConfigProperties::class.java)

    /**
     * Maximum number of days to keep the log entries
     */
    var applicationLogRetentionDays = 7
    /**
     * Number of fatal errors to notify into the GUI.
     *
     * @see ApplicationLogEntryLevel.FATAL
     */
    @Min(1)
    var applicationLogInfoMax = 10
    /**
     * Home directory
     */
    var applicationWorkingDir = "work/files"
    /**
     * Testing the configurations of external configurations
     */
    var configurationTest = true
    /**
     * Job configuration
     */
    @Valid
    var jobs: JobConfigProperties = JobConfigProperties()
    /**
     * Label provider collection job activation
     */
    var jobLabelProviderEnabled = false

    /**
     * Search configuration
     */
    var search = SearchConfigProperties()

    @PostConstruct
    fun log() {
        if (!configurationTest) {
            logger.warn("[config] Tests of external configurations are disabled")
        }
        logger.info("[search] Engine = ${search.engine}")
        logger.info("[search] Index immediate refresh = ${search.index.immediate}")
        logger.info("[search] Index batch size = ${search.index.batch}")
    }

    companion object {
        /**
         * Properties prefix
         */
        const val PREFIX = "ontrack.config"
        /**
         * Search service key
         */
        internal const val SEARCH = "search"
        /**
         * Search complete key
         */
        const val SEARCH_PROPERTY = "$PREFIX.$SEARCH"
        /**
         * Search engine property
         */
        const val SEARCH_ENGINE_PROPERTY = "$SEARCH_PROPERTY.${SearchConfigProperties.ENGINE_PROPERTY}"
        /**
         * Key store type
         */
        const val KEY_STORE = "ontrack.config.key-store"
    }
}