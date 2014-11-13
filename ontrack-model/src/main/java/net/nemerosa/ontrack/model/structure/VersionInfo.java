package net.nemerosa.ontrack.model.structure;

import lombok.Data;

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
}
