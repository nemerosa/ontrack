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
     * Display version. Example: 2.3 or master or feature/158-my-feature
     */
    private final String display;
    /**
     * Full version string, including the build number
     */
    private final String full;
    /**
     * Base version string, without the build number
     */
    private final String branch;
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
