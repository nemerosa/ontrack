package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.isProjectFunctionGranted
import net.nemerosa.ontrack.model.structure.PromotionLevelService
import net.nemerosa.ontrack.repository.PromotionLevelRepository
import org.springframework.stereotype.Service

@Service
class PromotionLevelServiceImpl(
    private val securityService: SecurityService,
    private val promotionLevelRepository: PromotionLevelRepository,
) : PromotionLevelService {

    override fun findPromotionLevelNames(token: String?): List<String> =
        promotionLevelRepository.findByToken(token)
            .filter { securityService.isProjectFunctionGranted<ProjectView>(it) }
            .groupBy { it.name }
            .keys
            .sorted()

}