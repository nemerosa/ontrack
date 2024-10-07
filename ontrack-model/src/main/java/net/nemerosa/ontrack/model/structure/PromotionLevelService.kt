package net.nemerosa.ontrack.model.structure

interface PromotionLevelService {

    fun findPromotionLevelNames(
        token: String?
    ): List<String>

    /**
     * Gets a list of enabled branches having the given promotion level name.
     *
     * The branches are ordered by last activity date (from the most recent to the oldest).
     */
    fun findBranchesWithPromotionLevel(
        project: Project,
        promotionLevelName: String,
        count: Int = 10,
    ): List<Branch>

}