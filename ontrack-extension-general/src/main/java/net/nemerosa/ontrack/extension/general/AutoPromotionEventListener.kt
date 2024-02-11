package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventListener
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.PromotionRun.Companion.of
import org.springframework.stereotype.Component

/**
 * When a new validation run is created with a Passed status, or when a promotion is granted, we check all auto promoted promotion levels
 * to know if each of their validation stamps is now passed.
 */
@Component
class AutoPromotionEventListener(
    private val structureService: StructureService,
    private val propertyService: PropertyService,
    private val securityService: SecurityService
) : EventListener {

    override fun onEvent(event: Event) {
        when {
            event.eventType === EventFactory.NEW_VALIDATION_RUN -> onNewValidationRun(event)
            event.eventType === EventFactory.DELETE_VALIDATION_STAMP -> onDeleteValidationStamp(event)
            event.eventType === EventFactory.NEW_PROMOTION_RUN -> onNewPromotionRun(event)
            event.eventType === EventFactory.DELETE_PROMOTION_LEVEL -> onDeletePromotionLevel(event)
        }
    }

    private fun onDeleteValidationStamp(event: Event) {
        // Gets the validation stamp ID
        val validationStampId = event.getIntValue("VALIDATION_STAMP_ID")
        // Branch
        val branch = event.getEntity<Branch>(ProjectEntityType.BRANCH)
        // Gets all promotion levels for this branch
        val promotionLevels = structureService.getPromotionLevelListForBranch(branch.id)
        // Checks all promotion levels
        promotionLevels.forEach { promotionLevel: PromotionLevel ->
            cleanPromotionLevelFromValidationStamp(
                promotionLevel,
                validationStampId
            )
        }
    }

    private fun onDeletePromotionLevel(event: Event) {
        // Gets the promotion level ID
        val promotionLevelId = event.getIntValue("PROMOTION_LEVEL_ID")
        // Branch
        val branch = event.getEntity<Branch>(ProjectEntityType.BRANCH)
        // Gets all promotion levels for this branch
        val promotionLevels = structureService.getPromotionLevelListForBranch(branch.id)
        // Checks all promotion levels
        promotionLevels.forEach { promotionLevel: PromotionLevel ->
            cleanPromotionLevelFromPromotionLevel(
                promotionLevel,
                promotionLevelId
            )
        }
    }

    private fun cleanPromotionLevelFromValidationStamp(promotionLevel: PromotionLevel, validationStampId: Int) {
        val oProperty = propertyService.getProperty(promotionLevel, AutoPromotionPropertyType::class.java).option()
        if (oProperty.isPresent) {
            val property = oProperty.get()
            val keptValidationStamps = property.validationStamps
                .filter { validationStamp: ValidationStamp -> (validationStampId != validationStamp.id()) }
            if (keptValidationStamps.size < property.validationStamps.size) {
                val editedProperty = AutoPromotionProperty(
                    keptValidationStamps,
                    property.include,
                    property.exclude,
                    property.promotionLevels
                )
                securityService.asAdmin {
                    propertyService.editProperty(
                        promotionLevel,
                        AutoPromotionPropertyType::class.java,
                        editedProperty
                    )
                }
            }
        }
    }

    private fun cleanPromotionLevelFromPromotionLevel(promotionLevel: PromotionLevel, promotionLevelId: Int) {
        val oProperty = propertyService.getProperty(promotionLevel, AutoPromotionPropertyType::class.java).option()
        if (oProperty.isPresent) {
            val property = oProperty.get()
            val keptPromotionLevels =
                property.promotionLevels.filter { pl: PromotionLevel -> (promotionLevelId != pl.id()) }
            if (keptPromotionLevels.size < property.promotionLevels.size) {
                val editedProperty = AutoPromotionProperty(
                    property.validationStamps,
                    property.include,
                    property.exclude,
                    keptPromotionLevels
                )
                securityService.asAdmin {
                    propertyService.editProperty(
                        promotionLevel,
                        AutoPromotionPropertyType::class.java,
                        editedProperty
                    )
                }
            }
        }
    }

    private fun onNewValidationRun(event: Event) {
        // Passed validation?
        val validationRun = event.getEntity<ValidationRun>(ProjectEntityType.VALIDATION_RUN)
        if (validationRun.lastStatus.statusID == ValidationRunStatusID.STATUS_PASSED) {
            processEvent(event)
        }
    }

    private fun onNewPromotionRun(event: Event) {
        processEvent(event)
    }

    private fun processEvent(event: Event) {
        // Branch
        val branch = event.getEntity<Branch>(ProjectEntityType.BRANCH)
        // Build
        val build = event.getEntity<Build>(ProjectEntityType.BUILD)
        // Gets all promotion levels for this branch
        val promotionLevels = structureService.getPromotionLevelListForBranch(branch.id)
        // Gets all validation stamps for this branch
        val validationStamps = structureService.getValidationStampListForBranch(branch.id)
        // Gets the promotion levels which have an auto promotion property
        promotionLevels.forEach { promotionLevel: PromotionLevel ->
            checkPromotionLevel(
                build,
                promotionLevel,
                promotionLevels,
                validationStamps
            )
        }
    }

    private fun checkPromotionLevel(
        build: Build,
        promotionLevel: PromotionLevel,
        promotionLevels: List<PromotionLevel>,
        validationStamps: List<ValidationStamp>
    ) {
        val oProperty = propertyService.getProperty(promotionLevel, AutoPromotionPropertyType::class.java).option()
        if (oProperty.isPresent) {
            val property = oProperty.get()
            // Checks if the property is eligible
            if (property.isEmpty()) {
                return
            }
            // Check to be done only if the promotion level is not attributed yet
            val runs = structureService.getPromotionRunsForBuildAndPromotionLevel(build, promotionLevel)
            if (runs.isEmpty()) {
                // Checks the status of each validation stamp
                val allVSPassed =
                    validationStamps // Keeps only the ones selectable for the autopromotion property
                        .filter { vs: ValidationStamp? ->
                            property.contains(
                                vs!!
                            )
                        } // They must all pass
                        .all { validationStamp: ValidationStamp ->
                            isValidationStampPassed(
                                build,
                                validationStamp
                            )
                        }
                // Checks that all needed promotions are granted
                val allPLPassed =
                    promotionLevels // Keeps only the ones selectable for the autopromotion property
                        .filter { pl: PromotionLevel? ->
                            property.contains(
                                pl!!
                            )
                        } // They must all be granted
                        .all { pl: PromotionLevel -> isPromotionLevelGranted(build, pl) }
                // Promotion is needed
                if (allVSPassed && allPLPassed) {
                    // Promotes
                    // Makes sure to raise the auth level because the one
                    // having made a validation might not be granted to
                    // creation a promotion
                    securityService.asAdmin {
                        structureService.newPromotionRun(
                            of(
                                build,
                                promotionLevel,
                                securityService.currentSignature,
                                "Auto promotion"
                            )
                        )
                    }
                }
            }
        }
    }

    private fun isValidationStampPassed(build: Build, validationStamp: ValidationStamp): Boolean {
        val runs = structureService.getValidationRunsForBuildAndValidationStamp(
            build.id,
            validationStamp.id,
            0,
            1,
            null,
            null
        )
        if (runs.isEmpty()) {
            return false
        } else {
            val run = runs[0]
            return run.lastStatus.statusID == ValidationRunStatusID.STATUS_PASSED
        }
    }

    private fun isPromotionLevelGranted(build: Build, promotionLevel: PromotionLevel): Boolean {
        val runs = structureService.getPromotionRunsForBuildAndPromotionLevel(build, promotionLevel)
        return !runs.isEmpty()
    }
}
