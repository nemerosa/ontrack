package net.nemerosa.ontrack.extension.svn.model

import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogIssue

class SVNChangeLogIssue(
        issue: Issue,
        val revisions: List<SVNRevisionInfo>
) : SCMChangeLogIssue(issue) {

    val lastRevision: SVNRevisionInfo?
        get() = if (revisions.isNotEmpty()) {
            revisions[revisions.size - 1]
        } else {
            null
        }

    constructor(issue: Issue) : this(issue, emptyList<SVNRevisionInfo>())

    fun addRevision(revision: SVNRevisionInfo) = SVNChangeLogIssue(issue, revisions + revision)

}
