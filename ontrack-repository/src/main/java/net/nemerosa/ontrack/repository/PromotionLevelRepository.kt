package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PromotionLevel

interface PromotionLevelRepository {

    fun findByToken(token: String?): List<PromotionLevel>

    fun findNamesByToken(token: String?): List<String>

    fun findBranchesWithPromotionLevel(
        project: Project,
        promotionLevelName: String,
    ): List<Branch>

}