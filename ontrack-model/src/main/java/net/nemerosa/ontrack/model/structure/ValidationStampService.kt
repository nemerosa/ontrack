package net.nemerosa.ontrack.model.structure

interface ValidationStampService {

    fun findValidationStampNames(
        token: String
    ): List<String>

}