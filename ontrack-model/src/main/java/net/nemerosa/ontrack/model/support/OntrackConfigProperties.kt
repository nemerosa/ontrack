package net.nemerosa.ontrack.model.support

import jakarta.annotation.PostConstruct
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIName
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


/**
 * Configuration properties for Ontrack.
 */
@Component
@ConfigurationProperties(prefix = OntrackConfigProperties.PREFIX)
@Validated
@APIName("General configuration")
@APIDescription("General configuration of Ontrack.")
class OntrackConfigProperties {

    private val logger: Logger = LoggerFactory.getLogger(OntrackConfigProperties::class.java)

    @APIDescription("Root URL for this Ontrack installation, used for notifications")
    var url: String = "http://localhost:3000"

    @APIDescription("Maximum number of days to keep the log entries")
    var applicationLogRetentionDays = 7

    @APIDescription("Disabling the collection of log entries in the application")
    var applicationLogEnabled = true

    /**
     * Number of fatal errors to notify into the GUI.
     *
     * @see ApplicationLogEntryLevel.FATAL
     */
    @Min(1)
    @APIDescription("Maximum number of errors to display as notifications in the GUI")
    var applicationLogInfoMax = 10

    @Min(1)
    @APIDescription("# Maximum number of builds which can be returned by a build filter. Any number above is truncated down to this value")
    var buildFilterCountMax = 200

    @APIDescription("Directory which contains all the working files of Ontrack. It is usually set by the installation.")
    var applicationWorkingDir = "work/files"

    @APIDescription("Testing the configurations of external configurations. Used only for internal testing, to disable the checks when creating external configurations.")
    var configurationTest = true

    /**
     * Job configuration
     */
    @Valid
    var jobs: JobConfigProperties = JobConfigProperties()

    @APIDescription("Activation of the provided labels collection job")
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

    /**
     * Key store settings
     */
    var fileKeyStore = FileKeyStoreProperties()

    /**
     * Templating settings
     */
    var templating = TemplatingProperties()

    /**
     * Authorization settings
     */
    var authorization = AuthorizationConfigProperties()

    /**
     * Key-store type
     */
    @APIDescription("Key store type to use to store encryption keys")
    var keyStore: String = "file"

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
        logger.info("[templating] Errors = ${templating.errors}")
    }

    /**
     * Authorization settings
     */
    class AuthorizationConfigProperties {
        /**
         * Initial provisioning
         */
        var provisioning = true
        /**
         * Initial admin properties
         */
        var admin = AdminConfigProperties()
    }

    class AdminConfigProperties {
        /**
         * Email
         */
        var email = "admin@ontrack.local"
        /**
         * Full name
         */
        var fullName = "Administrator"
        /**
         * Name of the group to assign (must exists or must be blank)
         */
        var groupName = "Administrators"
    }

    /**
     * File key store settings
     */
    class FileKeyStoreProperties {
        /**
         * Directory to use for the `file` confidential store
         * Optional. If not filled in, will use a subdirectory of the working directory
         */
        var directory: String = ""
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
     * Templating settings
     */
    class TemplatingProperties {
        /**
         * How to deal with templating errors?
         */
        var errors: TemplatingErrors = TemplatingErrors.IGNORE

        /**
         * HTML tags to accept on top of the default ones
         */
        @APIDescription("HTML tags to accept on top of the default ones")
        var htmlTags: List<String> = emptyList()
    }

    /**
     * How to deal with templating errors?
     */
    enum class TemplatingErrors {
        /**
         * Ignoring the errors (default)
         */
        IGNORE,

        /**
         * Message in the rendering
         */
        MESSAGE,

        /**
         * Logging the errors stacks (ok for testing) and using the message into the rendering
         */
        LOGGING_STACK,

        /**
         * Throwing the errors (not recommended)
         */
        THROW,
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
         * Default validity duration for the _transient_ tokens.
         *
         * If set to 0 or negative, the generated tokens do not expire.
         *
         * By default, the tokens do not expire.
         */
        @DurationUnit(ChronoUnit.MINUTES)
        var transientValidity: Duration = DEFAULT_TRANSIENT_VALIDITY

        /**
         * Allows the token to be used as passwords.
         */
        var password: Boolean = true

        /**
         * Cache properties
         */
        var cache = TokensCacheProperties()

        companion object {
            /**
             * Default duration for the transient tokens
             */
            val DEFAULT_TRANSIENT_VALIDITY: Duration = Duration.ofMinutes(30)
        }
    }

    /**
     * Token cache properties
     */
    @Deprecated("Will be removed in V5")
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