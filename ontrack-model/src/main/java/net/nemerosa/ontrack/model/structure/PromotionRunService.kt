package net.nemerosa.ontrack.model.structure

interface PromotionRunService {

    /**
     * For a [project], returns the last promotion run having occurred within the whole
     * project for a given promotion mentioned by name.
     */
    fun getLastPromotionRunForProject(project: Project, promotionName: String): PromotionRun?

    /**
     * For a [branch], returns the last promotion run having occurred for a given promotion mentioned by name.
     */
    fun getLastPromotionRunForBranch(branch: Branch, promotionName: String): PromotionRun?

    /**
     * Checks if the given [build] is promoted or not to the given [promotion level][promotionLevel].
     */
    fun isBuildPromoted(build: Build, promotionLevel: PromotionLevel): Boolean


}