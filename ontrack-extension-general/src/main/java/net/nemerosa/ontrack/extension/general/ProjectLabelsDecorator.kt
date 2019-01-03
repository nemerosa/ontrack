package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.extension.api.DecorationExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.labels.Label
import net.nemerosa.ontrack.model.labels.ProjectLabelManagementService
import net.nemerosa.ontrack.model.structure.Decoration
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component
import java.util.*

@Component
class ProjectLabelsDecorator(
        extensionFeature: GeneralExtensionFeature,
        private val projectLabelManagementService: ProjectLabelManagementService
) : AbstractExtension(extensionFeature), DecorationExtension<Label> {

    override fun getScope(): EnumSet<ProjectEntityType> {
        return EnumSet.of(ProjectEntityType.PROJECT)
    }

    override fun getDecorations(entity: ProjectEntity): List<Decoration<Label>> {
        return if (entity is Project) {
            val labelsForProject = projectLabelManagementService.getLabelsForProject(entity)
            labelsForProject.map { label ->
                Decoration.of(
                        this,
                        label
                )
            }
        } else {
            emptyList()
        }
    }

}
