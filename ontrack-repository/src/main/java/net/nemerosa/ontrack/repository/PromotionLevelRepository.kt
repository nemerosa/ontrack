package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.PromotionLevel

interface PromotionLevelRepository {

    fun findByToken(token: String?): List<PromotionLevel>

}