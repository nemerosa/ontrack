package net.nemerosa.ontrack.model.support

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.convert.DataSizeUnit
import org.springframework.boot.convert.DurationUnit
import org.springframework.stereotype.Component
import org.springframework.util.unit.DataSize
import org.springframework.util.unit.DataUnit
import org.springframework.validation.annotation.Validated
import java.time.Duration
import java.time.temporal.ChronoUnit
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
     * Maximum number of builds which can be returned by a build filter
     */
    @Min(1)
    var buildFilterCountMax = 200

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

    /**
     * Security configuration
     */
    var security = SecurityProperties()

    /**
     * Document storage properties
     */
    var documents = DocumentProperties()

    @PostConstruct
    fun log() {
        if (!configurationTest) {
            logger.warn("[config] Tests of external configurations are disabled")
        }
        logger.info("[security] Tokens validity: ${security.tokens.validity}")
        logger.info("[search] Index immediate refresh = ${search.index.immediate}")
        logger.info("[search] Index batch size = ${search.index.batch}")
        logger.info("[search] Index batch logging = ${search.index.logging}")
        logger.info("[search] Index batch tracing = ${search.index.tracing}")
        logger.info("[search] Index creation error ignoring = ${search.index.ignoreExisting}")
        logger.info("[document] Documents engine = ${documents.engine}")
    }

    /**
     * Document storage properties
     */
    class DocumentProperties {
        /**
         * Engine to be used
         */
        var engine: String = DEFAULT

        /**
         * Maximum size
         */
        @DataSizeUnit(DataUnit.KILOBYTES)
        var maxSize: DataSize = DataSize.ofKilobytes(16)

        /**
         * Properties & default values
         */
        companion object {
            /**
             * JDBC based
             */
            const val JDBC = "jdbc"
            /**
             * Default value
             */
            const val DEFAULT = JDBC
        }
    }

    /**
     * Security settings
     */
    class SecurityProperties {
        /**
         * Security token settings
         */
        val tokens = TokensProperties()
    }

    /**
     * Security token settings
     */
    class TokensProperties {
        /**
         * Default validity duration for the tokens.
         *
         * If set to 0 or negative, the generated tokens do not expire.
         *
         * By default, the tokens do not expire.
         */
        @DurationUnit(ChronoUnit.DAYS)
        var validity: Duration = Duration.ofDays(0)
        /**
         * Allows the token to be used as passwords.
         */
        var password: Boolean = true
        /**
         * Cache properties
         */
        var cache = TokensCacheProperties()
    }

    /**
     * Token cache properties
     */
    class TokensCacheProperties {
        /**
         * Is caching of the tokens enabled?
         */
        var enabled = true
        /**
         * Cache validity period
         */
        @DurationUnit(ChronoUnit.MINUTES)
        var validity: Duration = Duration.ofDays(30)
        /**
         * Maximum number of items in the cache. Should be aligned with the
         * number of sessions. Note that the objects stored in the cache are tiny.
         */
        var maxCount: Long = 1_000
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
         * Documents service key
         */
        internal const val DOCUMENTS = "documents"

        /**
         * Documents engine
         */
        const val DOCUMENTS_ENGINE = "$DOCUMENTS.engine"

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