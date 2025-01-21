package net.nemerosa.ontrack.service.settings

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.common.moveItem
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.PredefinedPromotionLevelService
import net.nemerosa.ontrack.model.structure.Entity.Companion.isEntityDefined
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.PredefinedPromotionLevel
import net.nemerosa.ontrack.model.structure.Reordering
import net.nemerosa.ontrack.model.support.ImageHelper.checkImage
import net.nemerosa.ontrack.repository.PredefinedPromotionLevelRepository
import org.apache.commons.lang3.Validate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PredefinedPromotionLevelServiceImpl(
    private val securityService: SecurityService,
    private val predefinedPromotionLevelRepository: PredefinedPromotionLevelRepository
) : PredefinedPromotionLevelService {

    override val predefinedPromotionLevels: List<PredefinedPromotionLevel>
        get() {
            securityService.checkGlobalFunction(GlobalSettings::class.java)
            return predefinedPromotionLevelRepository.predefinedPromotionLevels
        }

    override fun findPredefinedPromotionLevels(name: String): List<PredefinedPromotionLevel> {
        securityService.checkGlobalFunction(GlobalSettings::class.java)
        return predefinedPromotionLevelRepository.findPredefinedPromotionLevels(name)
    }

    override fun newPredefinedPromotionLevel(stamp: PredefinedPromotionLevel): PredefinedPromotionLevel {
        securityService.checkGlobalFunction(GlobalSettings::class.java)
        val id = predefinedPromotionLevelRepository.newPredefinedPromotionLevel(stamp)
        return getPredefinedPromotionLevel(id)
    }

    override fun getPredefinedPromotionLevel(id: ID): PredefinedPromotionLevel {
        return predefinedPromotionLevelRepository.getPredefinedPromotionLevel(id)
    }

    override fun getPredefinedPromotionLevelImage(id: ID): Document {
        // Checks access
        getPredefinedPromotionLevel(id)
        // Repository access
        return predefinedPromotionLevelRepository.getPredefinedPromotionLevelImage(id) ?: Document.EMPTY
    }

    override fun findPredefinedPromotionLevelByName(name: String): PredefinedPromotionLevel? {
        return predefinedPromotionLevelRepository.findPredefinedPromotionLevelByName(name)
    }

    override fun savePredefinedPromotionLevel(predefinedPromotionLevel: PredefinedPromotionLevel) {
        // Validation
        isEntityDefined(predefinedPromotionLevel, "Predefined promotion level must be defined")
        // Security
        securityService.checkGlobalFunction(GlobalSettings::class.java)
        // Repository
        predefinedPromotionLevelRepository.savePredefinedPromotionLevel(predefinedPromotionLevel)
    }

    override fun deletePredefinedPromotionLevel(predefinedPromotionLevelId: ID): Ack {
        Validate.isTrue(predefinedPromotionLevelId.isSet, "Predefined promotion level ID must be set")
        securityService.checkGlobalFunction(GlobalSettings::class.java)
        return predefinedPromotionLevelRepository.deletePredefinedPromotionLevel(predefinedPromotionLevelId)
    }

    override fun setPredefinedPromotionLevelImage(predefinedPromotionLevelId: ID, document: Document) {
        // Checks the image type
        checkImage(document)
        // Checks access
        securityService.checkGlobalFunction(GlobalSettings::class.java)
        // Repository
        predefinedPromotionLevelRepository.setPredefinedPromotionLevelImage(predefinedPromotionLevelId, document)
    }

    @Deprecated("Will be removed in V5")
    override fun reorderPromotionLevels(reordering: Reordering) {
        securityService.checkGlobalFunction(GlobalSettings::class.java)
        predefinedPromotionLevelRepository.reorderPredefinedPromotionLevels(reordering)
    }

    override fun reorderPromotionLevels(activeId: Int, overId: Int) {
        // Checks access
        securityService.checkGlobalFunction(GlobalSettings::class.java)
        // Getting the list of all entries
        val entries = predefinedPromotionLevels
        // Sorting them
        val reorderedEntries = moveItem(
            items = entries,
            activeKey = activeId,
            overKey = overId,
        ) { it.id() }
        // Getting the new ordering
        val reordering = Reordering(reorderedEntries.map { it.id() })
        predefinedPromotionLevelRepository.reorderPredefinedPromotionLevels(reordering)
    }
}
