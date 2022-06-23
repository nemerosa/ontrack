package net.nemerosa.ontrack.extension.dm.tse

import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.model.structure.PromotionRun

/**
 * Repository helper class to find the OLDEST builds after a given event.
 */
interface TimeSincePromotionHelper {

    /**
     * Finds the OLDEST build AFTER the [ref] build.
     *
     * @return ID (if found) of the build
     */
    fun findOldestBuildAfterBuild(ref: Build): Int?

    /**
     * Finds the OLDEST promotion to [promotion] AFTER the [ref] build.
     *
     * @return ID (if found) of the promotion run
     */
    fun findOldestPromotionAfterBuild(ref: Build, promotion: PromotionLevel): Int?
}