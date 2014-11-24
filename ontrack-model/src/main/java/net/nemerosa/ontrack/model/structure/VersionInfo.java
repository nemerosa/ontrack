package net.nemerosa.ontrack.model.structure;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;

/**
 * Information about the version of the application.
 */
@Data
public class VersionInfo {

    /**
     * Creation date
     */
    private final LocalDateTime date;
    /**
     * Full version string, including the build number
     */
    private final String full;
    /**
     * Base version string, without the build number
     */
    private final String base;
    /**
     * Build number
     */
    private final String build;
    /**
     * Associated commit (hash)
     */
    private final String commit;
    /**
     * Source of the version. It can be a tag (correct for a real release) or a developer environment.
     */
    private final String source;
    /**
     * Type of source for the version.
     */
    private final String sourceType;

    /**
     * Version to display
     */
    public String getMarketingVersion() {
        if ("release".equals(sourceType)) {
            return StringUtils.substringAfter(source, "/");
        } else if ("tag".equals(sourceType)) {
            return full;
        } else if (source.indexOf("/") > 0) {
            return StringUtils.substringAfter(source, "/");
        } else {
            return source;
        }
    }

    /**
     * Is this a release?
     */
    public boolean isRelease() {
        return "tag".equals(sourceType);
    }

    /**
     * Label for the source type
     */
    public String getLabel() {
        if ("release".equals(sourceType)) {
            return "RC";
        } else if ("tag".equals(sourceType)) {
            return "Release";
        } else if ("develop".equals(sourceType)) {
            return "Dev";
        } else if ("feature".equals(sourceType)) {
            return "Feature";
        } else if ("hotfix".equals(sourceType)) {
            return "Hot fix";
        } else {
            return StringUtils.capitalize(sourceType);
        }
    }
}
