package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.structure.RunInfo
import net.nemerosa.ontrack.model.structure.RunInfoInput
import net.nemerosa.ontrack.model.structure.RunInfoService
import net.nemerosa.ontrack.model.structure.RunnableEntityType
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/rest/structure/run-info")
class RunInfoController(
        private val runInfoService: RunInfoService
) : AbstractResourceController() {

    @PutMapping("{runnableEntityType}/{id}")
    fun setRunInfo(
            @PathVariable runnableEntityType: RunnableEntityType,
            @PathVariable id: Int,
            @RequestBody input: RunInfoInput
    ): RunInfo =
            runInfoService.setRunInfo(
                    runInfoService.getRunnableEntity(
                            runnableEntityType, id
                    ),
                    input)

    @GetMapping("{runnableEntityType}/{id}")
    fun getRunInfo(
            @PathVariable runnableEntityType: RunnableEntityType,
            @PathVariable id: Int
    ): RunInfo =
            runInfoService.getRunInfo(
                    runInfoService.getRunnableEntity(
                            runnableEntityType, id
                    )
            ) ?: RunInfo.empty()

    @DeleteMapping("{runnableEntityType}/{id}")
    fun deleteRunInfo(
            @PathVariable runnableEntityType: RunnableEntityType,
            @PathVariable id: Int
    ): Ack =
            runInfoService.deleteRunInfo(
                    runInfoService.getRunnableEntity(
                            runnableEntityType, id
                    )
            )

}