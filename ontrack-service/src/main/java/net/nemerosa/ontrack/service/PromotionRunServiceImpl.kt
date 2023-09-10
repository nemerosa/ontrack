package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PromotionRun
import net.nemerosa.ontrack.model.structure.PromotionRunService
import net.nemerosa.ontrack.repository.PromotionRunRepository
import org.springframework.stereotype.Service

@Service
class PromotionRunServiceImpl(
    private val promotionRunRepository: PromotionRunRepository,
): PromotionRunService {

    override fun getLastPromotionRunForProject(project: Project, promotionName: String): PromotionRun? =
        promotionRunRepository.getLastPromotionRunForProject(project, promotionName)

}