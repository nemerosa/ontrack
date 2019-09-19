package net.nemerosa.ontrack.extension.general

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.support.AbstractPropertyType
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.MultiStrings
import net.nemerosa.ontrack.model.form.YesNo
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component
import java.util.function.Function

@Component
class MainBuildLinksProjectPropertyType(
        extensionFeature: GeneralExtensionFeature
) : AbstractPropertyType<MainBuildLinksProjectProperty>(extensionFeature) {

    override fun getName(): String = "Main build links"

    override fun getDescription(): String = """
         List of project labels which describes the list of build links
         to display in a build links decoration.
    """.trimIndent()

    override fun getSupportedEntityTypes(): Set<ProjectEntityType> =
            setOf(ProjectEntityType.PROJECT)

    override fun canEdit(entity: ProjectEntity, securityService: SecurityService): Boolean =
            securityService.isProjectFunctionGranted(entity, ProjectEdit::class.java)

    override fun canView(entity: ProjectEntity, securityService: SecurityService): Boolean = true

    override fun getEditionForm(entity: ProjectEntity, value: MainBuildLinksProjectProperty?): Form {
        return Form.create()
                .with(
                        MultiStrings.of("labels")
                                .help("List of project labels identifying the build links to keep in decorations.")
                                .label("Project labels")
                                .value(value?.labels ?: emptyList<String>())
                )
                .with(
                        YesNo.of("overrideGlobal")
                                .label("Override global settings")
                                .help("Checked if the project settings override the global settings, without being merged.")
                                .value(value?.overrideGlobal ?: false)
                )
    }

    override fun fromClient(node: JsonNode): MainBuildLinksProjectProperty {
        return fromStorage(node)
    }

    override fun fromStorage(node: JsonNode): MainBuildLinksProjectProperty {
        return parse(node, MainBuildLinksProjectProperty::class.java)
    }

    override fun replaceValue(value: MainBuildLinksProjectProperty, replacementFunction: Function<String, String>): MainBuildLinksProjectProperty {
        return value
    }
}