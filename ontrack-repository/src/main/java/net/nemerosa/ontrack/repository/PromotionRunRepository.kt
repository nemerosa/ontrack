package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.model.structure.PromotionRun

interface PromotionRunRepository {

    /**
     * For a [project], returns the last promotion run having occurred within the whole
     * project for a given promotion mentioned by name.
     */
    fun getLastPromotionRunForProject(project: Project, promotionName: String): PromotionRun?

    /**
     * Checks if the given [build] is promoted or not to the given [promotion level][promotionLevel].
     */
    fun isBuildPromoted(build: Build, promotionLevel: PromotionLevel): Boolean
}