package net.nemerosa.ontrack.extension.scm.graphql

import net.nemerosa.ontrack.graphql.support.ListRef
import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("File change filter to unshare in a project")
data class UnshareSCMFileChangeFilterInput(
    @APIDescription("ID of the project")
    val projectId: Int,
    @APIDescription("Name of the filter")
    val name: String,
)
