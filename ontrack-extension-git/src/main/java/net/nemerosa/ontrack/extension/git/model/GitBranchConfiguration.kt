package net.nemerosa.ontrack.extension.git.model

import net.nemerosa.ontrack.extension.git.support.TagBuildNameGitCommitLink

class GitBranchConfiguration(

        /**
         * Main project's configuration
         */
        val configuration: GitConfiguration,

        /**
         * Default branch
         */
        val branch: String,

        /**
         * Configured link
         */
        val buildCommitLink: ConfiguredBuildGitCommitLink<*>,

        /**
         * Build overriding policy when synchronizing
         */
        val isOverride: Boolean = false,

        /**
         * Interval in minutes for build/tag synchronization
         */
        val buildTagInterval: Int = 0
) {

    companion object {

        fun of(configuration: GitConfiguration, branch: String): GitBranchConfiguration {
            return GitBranchConfiguration(
                    configuration,
                    branch,
                    TagBuildNameGitCommitLink.DEFAULT,
                    false,
                    0
            )
        }
    }
}
