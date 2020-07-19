package net.nemerosa.ontrack.extension.git.model

class GitBranchConfiguration
@JvmOverloads
constructor(

        /**
         * Main project's configuration
         */
        val configuration: GitConfiguration,

        /**
         * Default branch
         */
        val branch: String,

        /**
         * Pull request information
         */
        val pullRequest: GitPullRequest?,

        /**
         * Configured link
         */
        val buildCommitLink: ConfiguredBuildGitCommitLink<*>? = null,

        /**
         * Build overriding policy when synchronizing
         */
        val override: Boolean = false,

        /**
         * Interval in minutes for build/tag synchronization
         */
        val buildTagInterval: Int = 0
)
