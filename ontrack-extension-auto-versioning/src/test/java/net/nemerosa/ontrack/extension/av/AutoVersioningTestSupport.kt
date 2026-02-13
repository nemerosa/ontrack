package net.nemerosa.ontrack.extension.av

import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditQueryFilter
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditQueryService
import net.nemerosa.ontrack.it.waitUntil
import net.nemerosa.ontrack.model.structure.PromotionRun
import org.springframework.stereotype.Component
import kotlin.time.ExperimentalTime

@Component
class AutoVersioningTestSupport(
    private val autoVersioningAuditQueryService: AutoVersioningAuditQueryService,
) {

    @OptIn(ExperimentalTime::class)
    fun waitForAutoVersioningToBeDone(promotionRun: PromotionRun) {
        waitUntil(
            message = "Auto-versioning to be done for promotion run $promotionRun",
        ) {
            val entries = autoVersioningAuditQueryService.findByFilter(
                filter = AutoVersioningAuditQueryFilter(
                    source = promotionRun.project.name,
                    version = promotionRun.build.name,
                )
            )
            entries.isNotEmpty() && entries.all { !it.mostRecentState.state.isRunning }
        }
    }

}