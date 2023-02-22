package net.nemerosa.ontrack.extension.git.graphql

import net.nemerosa.ontrack.model.structure.Build

interface RecursiveChangeLogService {

    fun getBuildByCommit(commitHash: String): Build?

    fun getDepBuildByCommit(commitHash: String, projectName: String): Build?

}