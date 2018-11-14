package net.nemerosa.ontrack.extension.git.model

import net.nemerosa.ontrack.model.structure.BranchStatusView
import net.nemerosa.ontrack.model.structure.BuildView

/**
 * All the information about a commit in a Git configuration, with its links with all
 * the projects.
 */
class OntrackGitCommitInfo(
        /**
         * Basic info about the commit
         */
        val uiCommit: GitUICommit,

        /**
         * Collection of build views
         */
        val buildViews: Collection<BuildView>,

        /**
         * Collection of promotions for the branches
         */
        val branchStatusViews: Collection<BranchStatusView>

)
