package net.nemerosa.ontrack.extension.indicators.ui

import net.nemerosa.ontrack.model.structure.ID
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
            @PathVariable typeId: Int
    ) = projectIndicatorService.getUpdateFormForIndicator(projectId, typeId)


}