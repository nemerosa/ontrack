package net.nemerosa.ontrack.model.settings

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.PredefinedValidationStamp

/**
 * Management of predefined validation stamps.
 */
interface PredefinedValidationStampService {
    /**
     * Gets the list of predefined validation stamps.
     */
    val predefinedValidationStamps: List<PredefinedValidationStamp>

    /**
     * Gets the list of predefined validation stamps filtered with a name part
     */
    fun findPredefinedValidationStamps(name: String): List<PredefinedValidationStamp>

    /**
     * Creates a new predefined validation stamp.
     */
    fun newPredefinedValidationStamp(stamp: PredefinedValidationStamp): PredefinedValidationStamp

    /**
     * Gets a predefined validation stamp using its ID
     */
    fun getPredefinedValidationStamp(id: ID): PredefinedValidationStamp

    /**
     * Gets the image for a predefined validation stamp
     */
    fun getPredefinedValidationStampImage(id: ID): Document

    /**
     * Gets a predefined validation stamp using its name
     */
    fun findPredefinedValidationStampByName(name: String): PredefinedValidationStamp?

    /**
     * Updates the predefined validation stamp
     */
    fun savePredefinedValidationStamp(stamp: PredefinedValidationStamp)

    /**
     * Deletes a predefined validation stamp
     */
    fun deletePredefinedValidationStamp(predefinedValidationStampId: ID): Ack

    /**
     * Sets the image for a predefined validation stamp
     */
    fun setPredefinedValidationStampImage(predefinedValidationStampId: ID, document: Document)
}
