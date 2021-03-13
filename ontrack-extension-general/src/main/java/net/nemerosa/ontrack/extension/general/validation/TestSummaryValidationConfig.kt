package net.nemerosa.ontrack.extension.general.validation

import net.nemerosa.ontrack.model.structure.ValidationRunStatusID

/**
 * Validation configuration for a test summary.
 *
 * @property warningIfSkipped If set to true, the status is set to warning if there is at least one skipped test.
 */
data class TestSummaryValidationConfig(
        val warningIfSkipped: Boolean
) {
    fun computeStatus(data: TestSummaryValidationData): ValidationRunStatusID =
            when {
                data.failed > 0 -> ValidationRunStatusID.STATUS_FAILED
                data.skipped > 0 && warningIfSkipped -> ValidationRunStatusID.STATUS_WARNING
                else -> ValidationRunStatusID.STATUS_PASSED
            }
}
