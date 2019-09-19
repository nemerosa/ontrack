package net.nemerosa.ontrack.extension.git.model

import net.nemerosa.ontrack.git.model.GitBranchInfo
import net.nemerosa.ontrack.git.model.GitSynchronisationStatus

/**
 * Information about a Git repository and the ways to synchronise it.
 */
// TODO #532 Workaround
open class GitSynchronisationInfo(

        /**
         * Type of Git provided (github, basic, etc.)
         *
         * @see GitConfiguration
         */
        val type: String,

        /**
         * Name of the configuration
         */
        val name: String,

        /**
         * Remote URL
         */
        val remote: String,

        /**
         * Indexation interval
         */
        val indexationInterval: Int,

        /**
         * General status
         */
        val status: GitSynchronisationStatus,

        /**
         * Synchronisation status, index of commits per branch. If no branch is present, it
         * means that the repository was never synched.
         */
        val branches: List<GitBranchInfo>

)
