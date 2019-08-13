package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.structure.PromotionRun
import net.nemerosa.ontrack.model.structure.PromotionRunCheck
import net.nemerosa.ontrack.model.structure.PromotionRunCheckService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PromotionRunCheckServiceImpl(
        checks: List<PromotionRunCheck>
) : PromotionRunCheckService {

    private val sortedChecks = checks.sortedBy { it.order }

    /**
     * Checks all check components in turn.
     */
    override fun checkPromotionRunCreation(promotionRun: PromotionRun) {
        sortedChecks.forEach { it.checkPromotionRunCreation(promotionRun) }
    }
}
