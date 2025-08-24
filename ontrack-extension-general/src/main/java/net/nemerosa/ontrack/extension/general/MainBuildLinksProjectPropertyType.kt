package net.nemerosa.ontrack.extension.general

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.support.AbstractPropertyType
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component

@Component
class MainBuildLinksProjectPropertyType(
        extensionFeature: GeneralExtensionFeature
) : AbstractPropertyType<MainBuildLinksProjectProperty>(extensionFeature) {

    override val name: String = "Main build links"

    override val description: String = """
         List of project labels which describes the list of build links
         to display in a build links decoration.
    """.trimIndent()

    override val supportedEntityTypes: Set<ProjectEntityType> =
            setOf(ProjectEntityType.PROJECT)

    override fun canEdit(entity: ProjectEntity, securityService: SecurityService): Boolean =
            securityService.isProjectFunctionGranted(entity, ProjectEdit::class.java)

    override fun canView(entity: ProjectEntity, securityService: SecurityService): Boolean = true

    override fun fromClient(node: JsonNode): MainBuildLinksProjectProperty {
        return fromStorage(node)
    }

    override fun fromStorage(node: JsonNode): MainBuildLinksProjectProperty {
        return parse(node, MainBuildLinksProjectProperty::class)
    }

    override fun replaceValue(value: MainBuildLinksProjectProperty, replacementFunction: (String) -> String): MainBuildLinksProjectProperty {
        return value
    }
}