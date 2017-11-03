package net.nemerosa.ontrack.extension.general.validation

import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.YesNo
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import net.nemerosa.ontrack.model.form.Int as IntField

data class ThresholdConfig(
        val warningThreshold: Int?,
        val failureThreshold: Int?,
        val okIfGreater: Boolean = true
) {

    fun computeStatus(value: Int): ValidationRunStatusID =
            if (okIfGreater) {
                when {
                    failureThreshold != null && value <= failureThreshold -> ValidationRunStatusID.STATUS_FAILED
                    warningThreshold != null && value <= warningThreshold -> ValidationRunStatusID.STATUS_WARNING
                    else -> ValidationRunStatusID.STATUS_PASSED
                }
            } else {
                when {
                    failureThreshold != null && value >= failureThreshold -> ValidationRunStatusID.STATUS_FAILED
                    warningThreshold != null && value >= warningThreshold -> ValidationRunStatusID.STATUS_WARNING
                    else -> ValidationRunStatusID.STATUS_PASSED
                }
            }

}

fun ThresholdConfig?.toForm(): Form =
        Form.create()
                .with(
                        IntField.of("warningThreshold")
                                .label("Warning threshold")
                                .optional()
                                .value(this?.warningThreshold)
                )
                .with(
                        IntField.of("failureThreshold")
                                .label("Failure threshold")
                                .optional()
                                .value(this?.failureThreshold)
                )
                .with(
                        YesNo.of("okIfGreater")
                                .label("OK if greater")
                                .value(this?.okIfGreater ?: true)
                )
