package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.labels.Label
import net.nemerosa.ontrack.model.labels.LabelManagementService
import net.nemerosa.ontrack.model.labels.ProjectLabelForm
import net.nemerosa.ontrack.model.labels.ProjectLabelManagementService
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import net.nemerosa.ontrack.ui.resource.Resources
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@RestController
@RequestMapping("/rest/labels/projects")
class ProjectLabelController(
        private val structureService: StructureService,
        private val labelManagementService: LabelManagementService,
        private val projectLabelManagementService: ProjectLabelManagementService
) : AbstractResourceController() {

    @GetMapping("{projectId}")
    fun getLabelsForProject(@PathVariable projectId: Int): Resources<Label> {
        return Resources.of(
                projectLabelManagementService.getLabelsForProject(
                        structureService.getProject(ID.of(projectId))
                ),
                uri(on(this::class.java).getLabelsForProject(projectId))
        )
    }

    @PutMapping("{projectId}")
    fun setLabelsForProject(@PathVariable projectId: Int, @RequestBody form: ProjectLabelForm): Resources<Label> {
        projectLabelManagementService.associateProjectToLabels(
                structureService.getProject(ID.of(projectId)),
                form
        )
        return getLabelsForProject(projectId)
    }

    @PutMapping("{projectId}/assign/{labelId")
    fun associateProjectToLabel(@PathVariable projectId: ID, @PathVariable labelId: Int): Ack {
        val project = structureService.getProject(projectId)
        val label = labelManagementService.getLabel(labelId)
        projectLabelManagementService.associateProjectToLabel(project, label)
        return Ack.OK
    }

    @PutMapping("{projectId}/unassign/{labelId")
    fun unassociateProjectToLabel(@PathVariable projectId: ID, @PathVariable labelId: Int): Ack {
        val project = structureService.getProject(projectId)
        val label = labelManagementService.getLabel(labelId)
        projectLabelManagementService.unassociateProjectToLabel(project, label)
        return Ack.OK
    }

}