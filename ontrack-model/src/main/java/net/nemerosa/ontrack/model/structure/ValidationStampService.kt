package net.nemerosa.ontrack.model.structure

interface ValidationStampService {

    fun findValidationStampNames(
        token: String
    ): List<String>

    fun findBranchesWithValidationStamp(project: Project, validation: String, size: Int): List<Branch>

    /**
     * Given a branch, collects the list of validation stamps using the provided names
     */
    fun findValidationStampsForNames(
        branch: Branch,
        validationStamps: List<String>
    ): List<ValidationStamp>

}