package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.BuildLink

interface BuildLinkRepository {

    fun getCountQualifiedBuildsUsedBy(build: Build): Int
    fun getQualifiedBuildsUsedBy(build: Build): List<BuildLink>
    fun getQualifiedBuildsUsing(build: Build): List<BuildLink>

    fun createBuildLink(fromBuild: Build, toBuild: Build, qualifier: String)
    fun deleteBuildLink(fromBuild: Build, toBuild: Build, qualifier: String)

    fun isLinkedTo(build: Build, project: String, buildPattern: String? = null, qualifier: String? = null): Boolean
    fun isLinkedTo(build: Build, targetBuild: Build, qualifier: String? = null): Boolean
    fun isLinkedFrom(build: Build, project: String, buildPattern: String? = null, qualifier: String? = null): Boolean

    /**
     * Loops over ALL the build links. Use this method with care, mostly for external indexation.
     */
    fun forEachBuildLink(code: (from: Build, to: Build, qualifier: String) -> Unit)

}