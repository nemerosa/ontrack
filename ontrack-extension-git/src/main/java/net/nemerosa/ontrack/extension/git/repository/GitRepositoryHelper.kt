package net.nemerosa.ontrack.extension.git.repository

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project

interface GitRepositoryHelper {

    fun findBranchWithProjectAndGitBranch(project: Project, gitBranch: String): Int?

}