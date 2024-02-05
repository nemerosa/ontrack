package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.structure.Build

@APIDescription("Representation of a change log between two builds")
data class SCMChangeLog(
    @APIDescription("Boundary for the change log")
    val from: Build,
    @APIDescription("Boundary for the change log")
    val to: Build,
    @APIDescription("List of commits between the builds")
    val commits: List<SCMChangeLogCommit>,
    @APIDescription("List of issues between the builds")
    val issues: SCMChangeLogIssues?,
)
