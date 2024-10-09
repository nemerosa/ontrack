package net.nemerosa.ontrack.model.structure

interface ValidationStampService {

    fun findValidationStampNames(
        token: String
    ): List<String>

    fun findBranchesWithValidationStamp(project: Project, validation: String, size: Int): List<Branch>

}