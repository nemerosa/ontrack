package net.nemerosa.ontrack.extension.workflows.definition

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.SelfDocumented

/**
 * Reference to a parent node
 */
@SelfDocumented
data class WorkflowParentNode(
    @APIDescription("ID of the parent node")
    val id: String,
)
