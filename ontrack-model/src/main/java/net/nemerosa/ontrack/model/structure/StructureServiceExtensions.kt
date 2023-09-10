package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.common.getOrNull

/**
 * If the validation stamp designed by its name does not exist, creates it.
 */
fun StructureService.setupValidationStamp(
    branch: Branch,
    validationStampName: String,
    validationStampDescription: String,
): ValidationStamp {
    val vs = findValidationStampByName(branch.project.name, branch.name, validationStampName).getOrNull()
    return vs ?: newValidationStamp(
        ValidationStamp.of(branch, NameDescription.nd(validationStampName, validationStampDescription))
    )
}
