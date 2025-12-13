package net.nemerosa.ontrack.model.settings

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.PredefinedPromotionLevel

/**
 * Management of predefined promotion levels.
 */
interface PredefinedPromotionLevelService {
    /**
     * Gets the list of predefined promotion levels.
     */
    val predefinedPromotionLevels: List<PredefinedPromotionLevel>

    /**
     * Gets the list of predefined promotion levels filtered with a name part
     */
    fun findPredefinedPromotionLevels(name: String): List<PredefinedPromotionLevel>

    /**
     * Creates a new predefined promotion level.
     */
    fun newPredefinedPromotionLevel(stamp: PredefinedPromotionLevel): PredefinedPromotionLevel

    /**
     * Gets a predefined promotion level using its ID
     */
    fun getPredefinedPromotionLevel(id: ID): PredefinedPromotionLevel

    /**
     * Gets the image for a predefined promotion level
     */
    fun getPredefinedPromotionLevelImage(id: ID): Document

    /**
     * Gets a predefined promotion level using its name
     */
    fun findPredefinedPromotionLevelByName(name: String): PredefinedPromotionLevel?

    /**
     * Updates the predefined promotion level
     */
    fun savePredefinedPromotionLevel(predefinedPromotionLevel: PredefinedPromotionLevel)

    /**
     * Deletes a predefined promotion level
     */
    fun deletePredefinedPromotionLevel(predefinedPromotionLevelId: ID): Ack

    /**
     * Sets the image for a predefined promotion level
     */
    fun setPredefinedPromotionLevelImage(predefinedPromotionLevelId: ID, document: Document)

    /**
     * Reorder the predefined validation stamps
     */
    fun reorderPromotionLevels(activeId: Int, overId: Int)
}
