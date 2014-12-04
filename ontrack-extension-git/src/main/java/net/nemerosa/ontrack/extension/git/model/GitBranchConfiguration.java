package net.nemerosa.ontrack.extension.git.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Wither;
import net.nemerosa.ontrack.extension.git.support.TagBuildNameGitCommitLink;

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

    /**
     * Build overriding policy when synchronizing
     */
    private final boolean override;

    /**
     * Interval in minutes for build/tag synchronization
     */
    private final int buildTagInterval;

    public static GitBranchConfiguration of(GitConfiguration configuration, String branch) {
        return new GitBranchConfiguration(
                configuration,
                branch,
                TagBuildNameGitCommitLink.DEFAULT,
                false,
                0
        );
    }
}
