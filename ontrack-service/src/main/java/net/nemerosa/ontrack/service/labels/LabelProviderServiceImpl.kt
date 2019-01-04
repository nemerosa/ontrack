package net.nemerosa.ontrack.service.labels

import net.nemerosa.ontrack.model.labels.LabelProvider
import net.nemerosa.ontrack.model.labels.LabelProviderService
import net.nemerosa.ontrack.model.labels.ProjectLabelManagement
import net.nemerosa.ontrack.model.labels.description
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.repository.LabelRepository
import net.nemerosa.ontrack.repository.ProjectLabelRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class LabelProviderServiceImpl(
        private val providers: List<LabelProvider>,
        private val securityService: SecurityService,
        private val labelRepository: LabelRepository,
        private val projectLabelRepository: ProjectLabelRepository
) : LabelProviderService {

    private val index: Map<String, LabelProvider> = providers.associateBy { it::class.java.name }

    override fun getLabelProvider(id: String): LabelProvider? = index[id]

    override fun collectLabels(project: Project) {
        securityService.checkProjectFunction(project, ProjectLabelManagement::class.java)
        // Computes labels for every provider
        providers.forEach { provider ->
            collectLabels(project, provider)
        }
    }

    private fun collectLabels(project: Project, provider: LabelProvider) {
        // Gets all labels for this project and provider
        val labels = provider.getLabelsForProject(project)
        // ID of the provider
        val providerId = provider.description.id
        // Gets all existing labels for this provider
        val existingLabels = labelRepository.findLabelsByProvider(providerId)
        // New labels (forms not found in existing)
        labels.filter { form ->
            existingLabels.none { existing ->
                existing.sameThan(form)
            }
        }.forEach { form ->
            labelRepository.overrideLabel(form, providerId)
        }
        // Updates (existing, found in forms with different attributes)
        existingLabels.filter { existing ->
            labels.any { form ->
                // Same category/name, but different attributes
                existing.sameThan(form) && existing.toLabelForm() != form
            }
        }.forEach { label ->
            // Gets the corresponding form
            val form = labels.find { label.sameThan(it) }
            // Saves it
            if (form != null) {
                labelRepository.updateAndOverrideLabel(label.id, form, providerId)
            }
        }
        // Deleted associations
        val existingAssociations = projectLabelRepository.getLabelsForProject(project.id())
                .map { labelRepository.getLabel(it) }
                .filter { it.computedBy == providerId }
        existingAssociations.forEach { association ->
            val existingForm = labels.any {
                it.category == association.category && it.name == association.name
            }
            if (!existingForm) {
                projectLabelRepository.unassociateProjectToLabel(
                        project.id(),
                        association.id
                )
            }
        }
        // New or existing project associations
        labels.forEach { form ->
            // Gets record for this label
            labelRepository.findLabelByCategoryAndNameAndProvider(form.category, form.name, providerId)
                    ?.let { record ->
                        projectLabelRepository.associateProjectToLabel(
                                project.id(),
                                record.id
                        )
                    }
        }
    }
}