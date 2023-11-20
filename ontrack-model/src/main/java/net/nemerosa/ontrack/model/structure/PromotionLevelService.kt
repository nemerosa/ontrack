package net.nemerosa.ontrack.model.structure

interface PromotionLevelService {

    fun findPromotionLevelNames(
        token: String?
    ): List<String>

}