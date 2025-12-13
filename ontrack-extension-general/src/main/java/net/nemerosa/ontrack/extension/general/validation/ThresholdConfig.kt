package net.nemerosa.ontrack.extension.general.validation

import net.nemerosa.ontrack.model.structure.ValidationRunStatusID

data class ThresholdConfig(
        val warningThreshold: Int?,
        val failureThreshold: Int?,
        val okIfGreater: Boolean = true
) {

    fun computeStatus(value: Int): ValidationRunStatusID =
            if (okIfGreater) {
                when {
                    failureThreshold != null && value < failureThreshold -> ValidationRunStatusID.STATUS_FAILED
                    warningThreshold != null && value < warningThreshold -> ValidationRunStatusID.STATUS_WARNING
                    else -> ValidationRunStatusID.STATUS_PASSED
                }
            } else {
                when {
                    failureThreshold != null && value > failureThreshold -> ValidationRunStatusID.STATUS_FAILED
                    warningThreshold != null && value > warningThreshold -> ValidationRunStatusID.STATUS_WARNING
                    else -> ValidationRunStatusID.STATUS_PASSED
                }
            }

}
