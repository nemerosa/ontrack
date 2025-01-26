package net.nemerosa.ontrack.service.settings

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.security.GlobalSettings
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.settings.PredefinedValidationStampService
import net.nemerosa.ontrack.model.structure.Entity.Companion.isEntityDefined
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.PredefinedValidationStamp
import net.nemerosa.ontrack.model.support.ImageHelper.checkImage
import net.nemerosa.ontrack.repository.PredefinedValidationStampRepository
import org.apache.commons.lang3.Validate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PredefinedValidationStampServiceImpl(
    private val securityService: SecurityService,
    private val predefinedValidationStampRepository: PredefinedValidationStampRepository
) : PredefinedValidationStampService {

    override val predefinedValidationStamps: List<PredefinedValidationStamp>
        get() {
            securityService.checkGlobalFunction(GlobalSettings::class.java)
            return predefinedValidationStampRepository.predefinedValidationStamps
        }

    override fun findPredefinedValidationStamps(name: String): List<PredefinedValidationStamp> {
        securityService.checkGlobalFunction(GlobalSettings::class.java)
        return predefinedValidationStampRepository.findPredefinedValidationStamps(name)
    }

    override fun newPredefinedValidationStamp(stamp: PredefinedValidationStamp): PredefinedValidationStamp {
        securityService.checkGlobalFunction(GlobalSettings::class.java)
        val id = predefinedValidationStampRepository.newPredefinedValidationStamp(stamp)
        return getPredefinedValidationStamp(id)
    }

    override fun getPredefinedValidationStamp(id: ID): PredefinedValidationStamp {
        return predefinedValidationStampRepository.getPredefinedValidationStamp(id)
    }

    override fun getPredefinedValidationStampImage(id: ID): Document {
        // Checks access
        getPredefinedValidationStamp(id)
        // Repository access
        return predefinedValidationStampRepository.getPredefinedValidationStampImage(id)
    }

    override fun findPredefinedValidationStampByName(name: String): PredefinedValidationStamp? {
        return predefinedValidationStampRepository.findPredefinedValidationStampByName(name)
    }

    override fun savePredefinedValidationStamp(stamp: PredefinedValidationStamp) {
        // Validation
        isEntityDefined(stamp, "Predefined validation stamp must be defined")
        // Security
        securityService.checkGlobalFunction(GlobalSettings::class.java)
        // Repository
        predefinedValidationStampRepository.savePredefinedValidationStamp(stamp)
    }

    override fun deletePredefinedValidationStamp(predefinedValidationStampId: ID): Ack {
        Validate.isTrue(predefinedValidationStampId.isSet, "Predefined validation stamp ID must be set")
        securityService.checkGlobalFunction(GlobalSettings::class.java)
        return predefinedValidationStampRepository.deletePredefinedValidationStamp(predefinedValidationStampId)
    }

    override fun setPredefinedValidationStampImage(predefinedValidationStampId: ID, document: Document) {
        // Checks the image type
        checkImage(document)
        // Checks access
        securityService.checkGlobalFunction(GlobalSettings::class.java)
        // Repository
        predefinedValidationStampRepository.setPredefinedValidationStampImage(predefinedValidationStampId, document)
    }
}
