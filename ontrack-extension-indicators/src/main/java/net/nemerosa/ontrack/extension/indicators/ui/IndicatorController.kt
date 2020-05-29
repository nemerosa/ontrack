package net.nemerosa.ontrack.extension.indicators.ui

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import net.nemerosa.ontrack.ui.resource.Resources
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on

@RestController
@RequestMapping("/extension/indicators")
class IndicatorController(
        private val projectIndicatorService: ProjectIndicatorService
): AbstractResourceController()  {

    /**
     * Gets the list of ALL indicators for a project
     */
    @GetMapping("project/{projectId}")
    fun getAllProjectIndicators(@PathVariable projectId: ID): Resources<ProjectCategoryIndicators> = Resources.of(
            projectIndicatorService.getProjectCategoryIndicators(projectId, all = true),
            uri(on(this::class.java).getAllProjectIndicators(projectId))
    )

    /**
     * Gets the update form for a project indicator
     */
    @GetMapping("project/{projectId}/indicator/{typeId}/update")
    fun getUpdateFormForIndicator(
            @PathVariable projectId: ID,
            @PathVariable typeId: String
    ) = projectIndicatorService.getUpdateFormForIndicator(projectId, typeId)

    /**
     * Updates a project indicator
     */
    @PutMapping("project/{projectId}/indicator/{typeId}/update")
    fun updateIndicator(
            @PathVariable projectId: ID,
            @PathVariable typeId: String,
            @RequestBody input: JsonNode
    ) = projectIndicatorService.updateIndicator(projectId, typeId, input)

    /**
     * Deletes a project indicator
     */
    @DeleteMapping("project/{projectId}/indicator/{typeId}/delete")
    fun deleteIndicator(
            @PathVariable projectId: ID,
            @PathVariable typeId: String
    ) = projectIndicatorService.deleteIndicator(projectId, typeId)


}