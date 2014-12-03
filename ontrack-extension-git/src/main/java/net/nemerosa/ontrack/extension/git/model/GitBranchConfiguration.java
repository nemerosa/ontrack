package net.nemerosa.ontrack.extension.git.model;

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
    @Deprecated
    private final String branch;

    /**
     * Configured link
     */
    @Wither
    @Deprecated
    private final ConfiguredBuildGitCommitLink<?> buildCommitLink;

}
