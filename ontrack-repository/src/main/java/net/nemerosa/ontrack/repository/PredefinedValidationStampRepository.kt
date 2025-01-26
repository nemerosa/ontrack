package net.nemerosa.ontrack.repository

import net.nemerosa.ontrack.common.Document
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.PredefinedValidationStamp

interface PredefinedValidationStampRepository {
    val predefinedValidationStamps: List<PredefinedValidationStamp>

    fun findPredefinedValidationStamps(name: String): List<PredefinedValidationStamp>

    fun newPredefinedValidationStamp(stamp: PredefinedValidationStamp): ID

    fun getPredefinedValidationStamp(id: ID): PredefinedValidationStamp

    fun findPredefinedValidationStampByName(name: String): PredefinedValidationStamp?

    fun getPredefinedValidationStampImage(id: ID): Document

    fun savePredefinedValidationStamp(stamp: PredefinedValidationStamp)

    fun deletePredefinedValidationStamp(predefinedValidationStampId: ID): Ack

    fun setPredefinedValidationStampImage(predefinedValidationStampId: ID, document: Document)
}
