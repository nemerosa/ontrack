package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/structure/run-info")
class RunInfoController(
        private val structureService: StructureService,
        private val runInfoService: RunInfoService
) : AbstractResourceController() {

    @PutMapping("{runnableEntityType}/{id}")
    fun setRunInfo(
            @PathVariable runnableEntityType: RunnableEntityType,
            @PathVariable id: Int,
            @RequestBody input: RunInfoInput
    ): RunInfo =
            runInfoService.setRunInfo(runnableEntityType, id, input)

    @GetMapping("{runnableEntityType}/{id}")
    fun getRunInfo(
            @PathVariable runnableEntityType: RunnableEntityType,
            @PathVariable id: Int
    ): RunInfo =
            runInfoService.getRunInfo(runnableEntityType, id)

}