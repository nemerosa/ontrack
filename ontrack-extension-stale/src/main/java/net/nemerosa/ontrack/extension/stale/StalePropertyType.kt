package net.nemerosa.ontrack.extension.stale

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.support.AbstractPropertyType
import net.nemerosa.ontrack.model.security.ProjectConfig
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component
import java.util.*
import java.util.function.Function

@Component
class StalePropertyType(
    extensionFeature: StaleExtensionFeature,
) : AbstractPropertyType<StaleProperty>(extensionFeature) {

    override fun getName(): String = "Stale branches"

    override fun getDescription(): String = "Allows to disable or delete stale branches"

    override fun getSupportedEntityTypes(): Set<ProjectEntityType> = EnumSet.of(ProjectEntityType.PROJECT)

    override fun canEdit(entity: ProjectEntity, securityService: SecurityService): Boolean {
        return securityService.isProjectFunctionGranted(entity, ProjectConfig::class.java)
    }

    override fun canView(entity: ProjectEntity, securityService: SecurityService): Boolean = true


    override fun fromClient(node: JsonNode): StaleProperty {
        return fromStorage(node)
    }

    override fun fromStorage(node: JsonNode): StaleProperty {
        return parse(node, StaleProperty::class.java)
    }

    override fun replaceValue(value: StaleProperty, replacementFunction: Function<String, String>): StaleProperty {
        return value
    }
}