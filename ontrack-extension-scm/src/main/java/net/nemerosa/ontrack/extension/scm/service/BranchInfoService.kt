package net.nemerosa.ontrack.extension.scm.service

import net.nemerosa.ontrack.extension.scm.model.BranchInfos
import net.nemerosa.ontrack.model.structure.Project

interface BranchInfoService {

    /**
     * Given a project and a commit, returns the list of branches linked to this commit.
     */
    fun getBranchInfos(project: Project, commit: String): List<BranchInfos>

}