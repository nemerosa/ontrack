package net.nemerosa.ontrack.extension.github.workflow

import net.nemerosa.ontrack.extension.api.DecorationExtension
import net.nemerosa.ontrack.extension.github.GitHubExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.Decoration
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.PropertyService
import org.springframework.stereotype.Component
import java.util.*

@Component
class BuildGitHubWorkflowRunDecorator(
    extensionFeature: GitHubExtensionFeature,
    private val propertyService: PropertyService,
) : AbstractExtension(extensionFeature), DecorationExtension<BuildGitHubWorkflowRunProperty> {

    override fun getScope(): EnumSet<ProjectEntityType> = EnumSet.of(ProjectEntityType.BUILD)

    override fun getDecorations(entity: ProjectEntity): List<Decoration<BuildGitHubWorkflowRunProperty>> =
        listOfNotNull(
            propertyService.getProperty(entity, BuildGitHubWorkflowRunPropertyType::class.java).value
                ?.run {
                    Decoration.of(this@BuildGitHubWorkflowRunDecorator, this)
                }
        )
}