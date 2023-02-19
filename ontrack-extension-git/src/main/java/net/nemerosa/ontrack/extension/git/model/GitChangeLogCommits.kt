package net.nemerosa.ontrack.extension.git.model

import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogCommit
import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogCommits
import net.nemerosa.ontrack.model.structure.Decoration
import net.nemerosa.ontrack.model.structure.ViewSupplier

// TODO #532 Workaround
open class GitChangeLogCommits(
    val log: GitUILog
) : SCMChangeLogCommits, ViewSupplier {

    override fun getCommits(): List<SCMChangeLogCommit> {
        return log.commits
    }

    /**
     * Json view used to render decorations (builds, promotion runs...) on the [GitUICommit].
     */
    override fun getViewType(): Class<*> = Decoration::class.java
}
