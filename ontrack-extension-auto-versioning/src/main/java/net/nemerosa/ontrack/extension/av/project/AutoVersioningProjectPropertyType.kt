package net.nemerosa.ontrack.extension.av.project

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.av.AutoVersioningExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractPropertyType
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.security.ProjectConfig
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component
import java.util.function.Function

@Component
class AutoVersioningProjectPropertyType(
    extensionFeature: AutoVersioningExtensionFeature,
) : AbstractPropertyType<AutoVersioningProjectProperty>(
    extensionFeature
) {
    override fun getName(): String = "Auto-versioning"

    override fun getDescription(): String = "Auto-versioning rules at project level"

    override fun getSupportedEntityTypes(): Set<ProjectEntityType> = setOf(ProjectEntityType.PROJECT)

    override fun canEdit(entity: ProjectEntity, securityService: SecurityService): Boolean =
        securityService.isProjectFunctionGranted(entity, ProjectConfig::class.java)

    override fun canView(entity: ProjectEntity, securityService: SecurityService): Boolean = true

    override fun fromClient(node: JsonNode) = fromStorage(node)

    override fun fromStorage(node: JsonNode) = node.parse<AutoVersioningProjectProperty>()

    override fun replaceValue(
        value: AutoVersioningProjectProperty,
        replacementFunction: Function<String, String>
    ): AutoVersioningProjectProperty = value

}