package net.nemerosa.ontrack.model.support;

import lombok.Data;
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
     * Full version string, including the build number
     */
    private String full = "SOURCE";
    /**
     * Base version string, without the build number
     */
    private String base = "SOURCE";
    /**
     * Build number
     */
    private String build = "0";
    /**
     * Associated commit (hash)
     */
    private String commit = "NA";
    /**
     * Source of the version. It can be a tag (correct for a real release) or a developer environment.
     */
    private String source = "source";

    /**
     * Gets the representation of the version
     */
    public VersionInfo toInfo() {
        return new VersionInfo(
                parseDate(date),
                full,
                base,
                build,
                commit,
                source
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
