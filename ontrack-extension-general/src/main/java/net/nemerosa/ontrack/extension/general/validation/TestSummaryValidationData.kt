package net.nemerosa.ontrack.extension.general.validation

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Validation data associated to a CI test.
 *
 * @property passed Count of passed tests
 * @property skipped Count of skipped/ignored tests
 * @property failed Count of failed tests
 */
class TestSummaryValidationData(
        val passed: Int,
        val skipped: Int,
        val failed: Int
) {
    /**
     * Total count of tests (derived property)
     */
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    val total = passed + skipped + failed

}
