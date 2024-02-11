package net.nemerosa.ontrack.extension.scm.model

/**
 * Common attributes for a list of commits (or revisions) in a change log.
 */
@Deprecated("Will be removed in V5. Use the new scm.SCMChangeLogCommit")
interface SCMChangeLogCommits {
    /**
     * List of commits
     */
    val commits: List<SCMChangeLogCommit>
}
