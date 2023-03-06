package net.nemerosa.ontrack.extension.git.graphql

import net.nemerosa.ontrack.model.structure.Build

interface RecursiveChangeLogService {

    /**
     * Given a boundary on a project B, returns the boundaries for the dependency.
     *
     * Given a change log on project B
     * and two B1 & B2 build boundaries,
     * and a dependency A
     * take A1 dependency from B1
     * take A2 dependency from B2
     * display the change log from A1 to A2
     *
     * @param buildFrom B1 build
     * @param buildTo B2 build
     * @param depName A project dependency
     * @return Valid change log boundaries or null if not available
     */
    fun getDependencyChangeLog(
        buildFrom: Build,
        buildTo: Build,
        depName: String,
    ): Pair<Build, Build>?

    fun getBuildByCommit(commitHash: String): Build?

}