package net.nemerosa.ontrack.service;

import lombok.Data;
import net.nemerosa.ontrack.model.structure.VersionInfo;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Data
@Component
@ConfigurationProperties(prefix = "ontrack.version")
public class VersionInfoConfig {
    /**
     * Creation date
     */
    private LocalDateTime date;
    /**
     * Full version string, including the build number
     */
    private String full;
    /**
     * Base version string, without the build number
     */
    private String base;
    /**
     * Build number
     */
    private String build;
    /**
     * Associated commit (hash)
     */
    private String commit;
    /**
     * Source of the version. It can be a tag (correct for a real release) or a developer environment.
     */
    private String source;

    /**
     * Gets the representation of the version
     */
    public VersionInfo toInfo() {
        return new VersionInfo(
                date,
                full,
                base,
                build,
                commit,
                source
        );
    }
}
