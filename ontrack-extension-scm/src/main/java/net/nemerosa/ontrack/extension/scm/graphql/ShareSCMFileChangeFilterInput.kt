package net.nemerosa.ontrack.extension.scm.graphql

import net.nemerosa.ontrack.common.api.APIDescription
import net.nemerosa.ontrack.graphql.support.ListRef

@APIDescription("File change filter to share/edit in a project")
data class ShareSCMFileChangeFilterInput(
    @APIDescription("ID of the project")
    val projectId: Int,
    @APIDescription("Name of the filter")
    val name: String,
    @APIDescription("List of patterns")
    @ListRef
    val patterns: List<String>,
)
