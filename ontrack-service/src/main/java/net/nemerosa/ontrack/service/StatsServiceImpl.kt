package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.structure.StatsService
import net.nemerosa.ontrack.repository.StatsRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class StatsServiceImpl(
    private val statsRepository: StatsRepository
) : StatsService {

    override fun getProjectCount(): Int = statsRepository.projectCount

    override fun getBranchCount(): Int = statsRepository.branchCount

    override fun getBuildCount(): Int = statsRepository.buildCount

    override fun getPromotionLevelCount(): Int = statsRepository.promotionLevelCount

    override fun getPromotionRunCount(): Int = statsRepository.promotionRunCount

    override fun getValidationStampCount(): Int = statsRepository.validationStampCount

    override fun getValidationRunCount(): Int = statsRepository.validationRunCount

    override fun getValidationRunStatusCount(): Int = statsRepository.validationRunStatusCount

    override fun getPropertyCount(): Int = statsRepository.propertyCount

    override fun getEventCount(): Int = statsRepository.eventCount

}