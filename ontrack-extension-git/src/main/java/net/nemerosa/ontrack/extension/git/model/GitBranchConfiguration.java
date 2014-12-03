package net.nemerosa.ontrack.extension.git.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Wither;

@Data
@AllArgsConstructor
public class GitBranchConfiguration {

    /**
     * Main project's configuration
     */
    private final GitConfiguration configuration;

    /**
     * Default branch
     */
    @Wither
    private final String branch;

    /**
     * Configured link
     */
    @Wither
    private final ConfiguredBuildGitCommitLink<?> buildCommitLink;

    @JsonIgnore
    public boolean isValid() {
        return configuration.isValid();
    }
}
