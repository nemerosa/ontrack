package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.extension.scm.model.SCMChangeLogCommit
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.structure.Build

@APIDescription("Representation of a change log between two builds")
data class SCMChangeLog(
    @APIDescription("Boundary for the change log")
    val from: Build,
    @APIDescription("Boundary for the change log")
    val to: Build,
    @APIDescription("Boundary commit for the change log")
    val fromCommit: String,
    @APIDescription("Boundary commit for the change log")
    val toCommit: String,
    @APIDescription("List of commits between the builds")
    val commits: List<SCMDecoratedCommit>,
    @APIDescription("List of issues between the builds")
    val issues: SCMChangeLogIssues?,
) {
    fun isEmpty() = fromCommit == toCommit
}
