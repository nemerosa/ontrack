package net.nemerosa.ontrack.model.support;

import lombok.Data;
import net.nemerosa.ontrack.common.Time;
import net.nemerosa.ontrack.model.structure.VersionInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Data
@Component
@ConfigurationProperties(prefix = "ontrack.version")
public class VersionInfoConfig {

    /**
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(VersionInfoConfig.class);

    /**
     * Creation date
     */
    private String date;
    /**
     * Display version. Example: 2.3 or master or feature/158-my-feature
     */
    private String display = "n/a";
    /**
     * Full version string, including the build number. Example: release-2.0-10a1bb7
     */
    private String full = "n/a";
    /**
     * Branch for the version. Example: release-2.0 or master
     */
    private String branch = "n/a";
    /**
     * Build number. Example: 10a1bb7
     */
    private String build = "0";
    /**
     * Associated commit (hash). Example: 10a1bb77276321bef16abd7dcf19a5533ab8bd97
     */
    private String commit = "NA";
    /**
     * Source of the version.
     * Example: release/2.0
     */
    private String source = "source";
    /**
     * Type of source for the version.
     * Example: release
     */
    private String sourceType = "sourceType";

    /**
     * Gets the representation of the version
     */
    public VersionInfo toInfo() {
        return new VersionInfo(
                parseDate(date),
                display,
                full,
                branch,
                build,
                commit,
                source,
                sourceType
        );
    }

    /**
     * Parses the date defined in the application configuration properties as <code>ontrack.version.date</code>.
     */
    public static LocalDateTime parseDate(String value) {
        if (StringUtils.isBlank(value)) {
            logger.info("[version-info] No date defined, using current date.");
            return Time.now();
        } else {
            try {
                return LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
            } catch (DateTimeParseException ex) {
                logger.warn("[version-info] Wrong date format, using current date: " + value);
                return Time.now();
            }
        }
    }
}
