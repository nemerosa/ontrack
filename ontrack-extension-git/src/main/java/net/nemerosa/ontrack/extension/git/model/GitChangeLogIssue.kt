package net.nemerosa.ontrack.extension.git.model

import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogIssue

class GitChangeLogIssue(
        issue: Issue,
        val commits: MutableList<GitUICommit>
) : SCMChangeLogIssue(issue) {

    fun add(uiCommit: GitUICommit): GitChangeLogIssue {
        commits.add(uiCommit)
        return this
    }

    companion object {

        fun of(issue: Issue, uiCommit: GitUICommit): GitChangeLogIssue {
            return GitChangeLogIssue(
                    issue,
                    mutableListOf(uiCommit)
            )
        }
    }

}
