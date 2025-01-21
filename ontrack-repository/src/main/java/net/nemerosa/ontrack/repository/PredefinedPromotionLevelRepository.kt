package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.PredefinedPromotionLevel
import net.nemerosa.ontrack.model.structure.Reordering

interface PredefinedPromotionLevelRepository {

    val predefinedPromotionLevels: List<PredefinedPromotionLevel>

    fun findPredefinedPromotionLevels(name: String): List<PredefinedPromotionLevel>

    fun newPredefinedPromotionLevel(stamp: PredefinedPromotionLevel): ID

    fun getPredefinedPromotionLevel(id: ID): PredefinedPromotionLevel

    fun findPredefinedPromotionLevelByName(name: String): PredefinedPromotionLevel?

    fun getPredefinedPromotionLevelImage(id: ID): Document?

    fun savePredefinedPromotionLevel(predefinedPromotionLevel: PredefinedPromotionLevel)

    fun deletePredefinedPromotionLevel(predefinedPromotionLevelId: ID): Ack

    fun setPredefinedPromotionLevelImage(predefinedPromotionLevelId: ID, document: Document)

    fun reorderPredefinedPromotionLevels(reordering: Reordering)
}
