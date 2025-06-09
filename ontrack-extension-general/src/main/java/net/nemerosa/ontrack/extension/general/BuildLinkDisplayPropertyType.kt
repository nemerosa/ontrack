package net.nemerosa.ontrack.extension.general

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
class BuildLinkDisplayPropertyType(
        extensionFeature: GeneralExtensionFeature
) : AbstractPropertyType<BuildLinkDisplayProperty>(extensionFeature) {

    override fun getName(): String {
        return "Build link display options"
    }

    override fun getDescription(): String {
        return "Configuration of display options for the build links towards this project."
    }

    override fun getSupportedEntityTypes(): Set<ProjectEntityType> {
        return EnumSet.of(ProjectEntityType.PROJECT)
    }

    override fun canEdit(entity: ProjectEntity, securityService: SecurityService): Boolean {
        return securityService.isProjectFunctionGranted(entity, ProjectConfig::class.java)
    }

    override fun canView(entity: ProjectEntity, securityService: SecurityService): Boolean {
        return true
    }

    override fun fromClient(node: JsonNode): BuildLinkDisplayProperty {
        return fromStorage(node)
    }

    override fun fromStorage(node: JsonNode): BuildLinkDisplayProperty {
        return AbstractPropertyType.parse(node, BuildLinkDisplayProperty::class.java)
    }

    override fun replaceValue(value: BuildLinkDisplayProperty, replacementFunction: Function<String, String>): BuildLinkDisplayProperty {
        return value
    }
}
