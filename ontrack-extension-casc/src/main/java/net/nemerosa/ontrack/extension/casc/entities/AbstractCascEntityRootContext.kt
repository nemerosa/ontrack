package net.nemerosa.ontrack.extension.casc.entities

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.structure.ProjectEntity

abstract class AbstractCascEntityRootContext(
    private val propertiesContext: CascEntityPropertiesContext,
) : AbstractCascEntityContext(), CascEntityRootContext {

    override fun run(entity: ProjectEntity, node: JsonNode, paths: List<String>) {
        run(
            entity,
            node,
            paths,
            "properties" to propertiesContext,
        )
    }
}