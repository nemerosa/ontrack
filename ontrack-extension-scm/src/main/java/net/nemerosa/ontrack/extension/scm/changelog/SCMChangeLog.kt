package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.model.structure.Build

data class SCMChangeLog(
    val from: Build,
    val to: Build,
    val commits: List<SCMChangeLogCommit>,
    val issues: List<Issue>,
)
