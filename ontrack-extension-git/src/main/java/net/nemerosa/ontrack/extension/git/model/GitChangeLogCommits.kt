package net.nemerosa.ontrack.extension.git.model

import net.nemerosa.ontrack.extension.scm.changelog.SCMChangeLogCommit
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogCommits

// TODO #532 Workaround
open class GitChangeLogCommits(
    val log: GitUILog
) : SCMChangeLogCommits {

    override val commits: List<SCMChangeLogCommit> = log.commits

}
