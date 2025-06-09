package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.labels.Label
import net.nemerosa.ontrack.model.labels.LabelManagementService
import net.nemerosa.ontrack.model.labels.ProjectLabelForm
import net.nemerosa.ontrack.model.labels.ProjectLabelManagementService
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest/labels/projects")
class ProjectLabelController(
    private val structureService: StructureService,
    private val labelManagementService: LabelManagementService,
    private val projectLabelManagementService: ProjectLabelManagementService
) {

    @GetMapping("{projectId}")
    fun getLabelsForProject(@PathVariable projectId: Int): List<Label> {
        return projectLabelManagementService.getLabelsForProject(
            structureService.getProject(ID.of(projectId))
        )
    }

    @PutMapping("{projectId}")
    fun setLabelsForProject(@PathVariable projectId: Int, @RequestBody form: ProjectLabelForm): List<Label> {
        projectLabelManagementService.associateProjectToLabels(
            structureService.getProject(ID.of(projectId)),
            form
        )
        return getLabelsForProject(projectId)
    }

    @PutMapping("{projectId}/assign/{labelId}")
    fun associateProjectToLabel(@PathVariable projectId: ID, @PathVariable labelId: Int): Ack {
        val project = structureService.getProject(projectId)
        val label = labelManagementService.getLabel(labelId)
        projectLabelManagementService.associateProjectToLabel(project, label)
        return Ack.OK
    }

    @PutMapping("{projectId}/unassign/{labelId}")
    fun unassociateProjectToLabel(@PathVariable projectId: ID, @PathVariable labelId: Int): Ack {
        val project = structureService.getProject(projectId)
        val label = labelManagementService.getLabel(labelId)
        projectLabelManagementService.unassociateProjectToLabel(project, label)
        return Ack.OK
    }

}