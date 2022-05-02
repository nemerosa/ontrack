package net.nemerosa.ontrack.extension.casc.entities

import net.nemerosa.ontrack.model.structure.ProjectEntityType

interface CascEntityRootContext: CascEntityContext {

    /**
     * Entity type
     */
    val entityType: ProjectEntityType

}