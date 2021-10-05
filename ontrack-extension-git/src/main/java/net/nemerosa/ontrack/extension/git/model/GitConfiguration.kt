package net.nemerosa.ontrack.extension.git.model

import net.nemerosa.ontrack.extension.issues.model.ConfiguredIssueService
import net.nemerosa.ontrack.model.support.UserPassword

/**
 * Definition of a Git configuration.
 */
interface GitConfiguration {

    /**
     * Type
     */
    val type: String

    /**
     * Name in the type
     */
    val name: String

    /**
     * Remote URL-ish
     */
    val remote: String

    /**
     * Credentials
     */
    @Deprecated("One cannot use user/password in all cases. This method will be deleted in the context of issue #881.")
    val credentials: UserPassword?

    /**
     * Link to a commit, using {commit} as placeholder
     */
    val commitLink: String

    /**
     * Link to a file at a given commit, using {commit} and {path} as placeholders
     */
    val fileAtCommitLink: String

    /**
     * Indexation interval
     */
    val indexationInterval: Int

    /**
     * Gets the associated issue service configuration (if any)
     */
    val configuredIssueService: ConfiguredIssueService?

}
