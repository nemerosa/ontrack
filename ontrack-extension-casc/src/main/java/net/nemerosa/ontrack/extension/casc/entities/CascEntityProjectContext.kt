package net.nemerosa.ontrack.extension.casc.entities

import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component

@Component
class CascEntityProjectContext(
    propertiesContext: CascEntityPropertiesContext,

    ) : AbstractCascEntityRootContext(propertiesContext) {

    override val entityType: ProjectEntityType = ProjectEntityType.PROJECT

}