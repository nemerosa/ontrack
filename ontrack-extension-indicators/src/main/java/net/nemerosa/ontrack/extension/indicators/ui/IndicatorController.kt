package net.nemerosa.ontrack.extension.indicators.ui

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.structure.ID
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/extension/indicators")
class IndicatorController(
        private val projectIndicatorService: ProjectIndicatorService
) {

    /**
     * Gets the list of ALL indicators for a project
     */
    @GetMapping("project/{projectId}")
    fun getAllProjectIndicators(@PathVariable projectId: ID) =
            projectIndicatorService.getProjectIndicators(projectId, all = true)

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