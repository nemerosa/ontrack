package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.Signature
import net.nemerosa.ontrack.model.support.NameValue

data class UIEvent(
    val eventType: String,
    val template: String,
    val signature: Signature,
    val entities: Map<ProjectEntityType, ProjectEntity>,
    val extraEntities: Map<ProjectEntityType, ProjectEntity>,
    val ref: ProjectEntityType,
    val values: Map<String, NameValue>,
    /**
     * HTML rendering
     */
    val html: String,
    /**
     * Additional data processed from the values or entities
     */
    val data: Map<String, *>
)