package net.nemerosa.ontrack.extension.scm.index

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.Project

interface SCMBuildCommitIndexService {

    fun findEarliestBuildAfterCommit(branch: Branch, commit: String): Build?

    fun indexBuildCommits(project: Project): Int

    fun indexBuildCommit(build: Build, commit: String)

    fun getBuildCommit(build: Build): SCMBuildCommitIndexData?

    fun clearBuildCommits()

}