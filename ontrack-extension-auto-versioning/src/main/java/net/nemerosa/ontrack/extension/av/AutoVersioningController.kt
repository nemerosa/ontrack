package net.nemerosa.ontrack.extension.av

import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditQueryFilter
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditQueryService
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditState
import net.nemerosa.ontrack.extension.av.validation.AutoVersioningValidationData
import net.nemerosa.ontrack.extension.av.validation.AutoVersioningValidationService
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import net.nemerosa.ontrack.ui.resource.Resource
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder

@RestController
@RequestMapping("/extension/auto-versioning")
class AutoVersioningController(
    private val structureService: StructureService,
    private val autoVersioningAuditQueryService: AutoVersioningAuditQueryService,
    private val autoVersioningValidationService: AutoVersioningValidationService,
) : AbstractResourceController() {
    /**
     * Gets some statistics about the processing of PR creation orders.
     */
    @GetMapping("stats")
    fun getStats(): Resource<AutoVersioningStats> =
        Resource.of(
            AutoVersioningStats(
                pendingOrders = autoVersioningAuditQueryService.countByFilter(
                    filter = AutoVersioningAuditQueryFilter(
                        states = setOf(AutoVersioningAuditState.CREATED),
                    )
                )
            ),
            uri(MvcUriComponentsBuilder.on(AutoVersioningController::class.java).getStats())
        )

    /**
     * Launches auto versioning check on a build
     */
    @PostMapping("build/{buildId}/check")
    fun autoVersioningCheck(@PathVariable buildId: Int): List<AutoVersioningValidationData> {
        val build = structureService.getBuild(ID.of(buildId))
        return autoVersioningValidationService.checkAndValidate(build)
    }
}