package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PromotionRun

interface PromotionRunRepository {

    /**
     * For a [project], returns the last promotion run having occurred within the whole
     * project for a given promotion mentioned by name.
     */
    fun getLastPromotionRunForProject(project: Project, promotionName: String): PromotionRun?
}