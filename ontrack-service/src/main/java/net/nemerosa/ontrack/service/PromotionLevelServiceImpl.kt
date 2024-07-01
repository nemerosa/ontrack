package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.structure.PromotionLevelService
import net.nemerosa.ontrack.repository.PromotionLevelRepository
import org.springframework.stereotype.Service

@Service
class PromotionLevelServiceImpl(
    private val promotionLevelRepository: PromotionLevelRepository,
) : PromotionLevelService {

    override fun findPromotionLevelNames(token: String?): List<String> =
        promotionLevelRepository.findNamesByToken(token)
            .sorted()

}