package net.nemerosa.ontrack.extension.tfc.service

import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.ValidationRun

data class TFCValidationResult(
    val parameters: TFCParameters,
    val build: Build?,
    val validationRun: ValidationRun?,
)