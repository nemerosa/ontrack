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

    override val name: String = "Build link display options"

    override val description: String = "Configuration of display options for the build links towards this project."

    override val supportedEntityTypes: Set<ProjectEntityType> = EnumSet.of(ProjectEntityType.PROJECT)

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
        return parse(node, BuildLinkDisplayProperty::class)
    }

    @Deprecated("Will be removed in V5")
    override fun replaceValue(value: BuildLinkDisplayProperty, replacementFunction: Function<String, String>): BuildLinkDisplayProperty {
        return value
    }
}
