package net.nemerosa.ontrack.model.structure

interface PromotionRunService {

    /**
     * For a [project], returns the last promotion run having occurred within the whole
     * project for a given promotion mentioned by name.
     */
    fun getLastPromotionRunForProject(project: Project, promotionName: String): PromotionRun?


}