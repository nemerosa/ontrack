package net.nemerosa.ontrack.service.labels

import net.nemerosa.ontrack.model.labels.MainBuildLinksFilterService
import net.nemerosa.ontrack.model.labels.ProjectLabelManagementService
import net.nemerosa.ontrack.model.structure.Build
import org.springframework.stereotype.Service

@Service
class MainBuildLinksFilterServiceImpl(
        private val projectLabelManagementService: ProjectLabelManagementService
) : MainBuildLinksFilterService {

    override fun isMainBuidLink(target: Build, labels: List<String>): Boolean {
        // Gets the labels of the target's project
        val targetLabels = projectLabelManagementService.getLabelsForProject(target.project)
        // Gets those labels as strings
        val targetLabelNames = targetLabels.map { it.getDisplay() }
        // The target is a main link if the intersection between requested and actual list is not empty
        return labels.intersect(targetLabelNames).isNotEmpty()
    }

}