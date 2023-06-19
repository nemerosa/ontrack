package net.nemerosa.ontrack.model.support

import net.nemerosa.ontrack.common.Time.now
import net.nemerosa.ontrack.model.structure.VersionInfo
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Component
@ConfigurationProperties(prefix = "ontrack.version")
class VersionInfoConfig {

    /**
     * Creation date
     */
    var date: String? = null

    /**
     * Display version. Example: 2.3 or master or feature/158-my-feature
     */
    var display = "n/a"

    /**
     * Full version string, including the build number. Example: release-2.0-10a1bb7
     */
    var full = "n/a"

    /**
     * Branch for the version. Example: release-2.0 or master
     */
    var branch = "n/a"

    /**
     * Build number. Example: 10a1bb7
     */
    var build = "0"

    /**
     * Associated commit (hash). Example: 10a1bb77276321bef16abd7dcf19a5533ab8bd97
     */
    var commit = "NA"

    /**
     * Source of the version.
     * Example: release/2.0
     */
    var source = "source"

    /**
     * Type of source for the version.
     * Example: release
     */
    var sourceType = "sourceType"

    /**
     * Gets the representation of the version
     */
    fun toInfo(): VersionInfo = VersionInfo(
        parseDate(date),
        display,
        full,
        branch,
        build,
        commit,
        source,
        sourceType
    )

    companion object {

        /**
         * Logger
         */
        private val logger = LoggerFactory.getLogger(VersionInfoConfig::class.java)

        /**
         * Parses the date defined in the application configuration properties as `ontrack.version.date`.
         */
        fun parseDate(value: String?): LocalDateTime {
            return if (value.isNullOrBlank()) {
                logger.info("[version-info] No date defined, using current date.")
                now()
            } else {
                try {
                    LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
                } catch (ex: DateTimeParseException) {
                    logger.warn("[version-info] Wrong date format, using current date: $value")
                    now()
                }
            }
        }
    }
}
