package net.nemerosa.ontrack.extension.general.validation

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APILabel
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID

/**
 * Validation configuration for a test summary.
 *
 * @property warningIfSkipped If set to true, the status is set to warning if there is at least one skipped test.
 * @property failWhenNoResults If set to true, the status is set to failure if there is are no test at all.
 */
data class TestSummaryValidationConfig(
    @APILabel("Warning if skipped")
    @APIDescription("If set, the status is set to warning if there is at least one skipped test.")
    val warningIfSkipped: Boolean = false,
    @APILabel("Failing if no tests")
    @APIDescription("If set, the status is set to failure if there is are no test at all.")
    val failWhenNoResults: Boolean = false,
) {
    fun computeStatus(data: TestSummaryValidationData): ValidationRunStatusID =
        when {
            data.failed > 0 -> ValidationRunStatusID.STATUS_FAILED
            data.skipped > 0 && warningIfSkipped -> ValidationRunStatusID.STATUS_WARNING
            data.total == 0 && failWhenNoResults -> ValidationRunStatusID.STATUS_FAILED
            else -> ValidationRunStatusID.STATUS_PASSED
        }
}
