package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PromotionLevel

interface PromotionLevelRepository {

    fun findByToken(token: String?): List<PromotionLevel>

    fun findNamesByToken(token: String?): List<String>

    /**
     * Given a token, returns a list of promotion levels in the given project whose name
     * matches this token.
     */
    fun findPromotionLevelNamesByProject(project: Project, token: String?): List<String>

    fun findBranchesWithPromotionLevel(
        project: Project,
        promotionLevelName: String,
    ): List<Branch>

}