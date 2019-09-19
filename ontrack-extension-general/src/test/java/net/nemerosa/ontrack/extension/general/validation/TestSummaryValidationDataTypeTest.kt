package net.nemerosa.ontrack.extension.general.validation

import net.nemerosa.ontrack.extension.general.GeneralExtensionFeature
import net.nemerosa.ontrack.json.jsonOf
import net.nemerosa.ontrack.model.exceptions.ValidationRunDataInputException
import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class TestSummaryValidationDataTypeTest {

    private val dataType = TestSummaryValidationDataType(GeneralExtensionFeature())

    @Test
    fun toJson() {
        val json = dataType.toJson(
                TestSummaryValidationData(12, 50, 24)
        )
        assertEquals(12, json["passed"].asInt())
        assertEquals(50, json["skipped"].asInt())
        assertEquals(24, json["failed"].asInt())
        assertEquals(86, json["total"].asInt())
    }

    @Test
    fun fromJsonPartial() {
        val data = dataType.fromJson(
                jsonOf(
                        "passed" to 12,
                        "skipped" to 50,
                        "failed" to 24
                )
        )
        assertEquals(12, data?.passed)
        assertEquals(50, data?.skipped)
        assertEquals(24, data?.failed)
        assertEquals(86, data?.total)
    }

    @Test
    fun fromJsonComplete() {
        val data = dataType.fromJson(
                jsonOf(
                        "passed" to 12,
                        "skipped" to 50,
                        "failed" to 24,
                        "total" to 0
                )
        )
        assertEquals(12, data?.passed)
        assertEquals(50, data?.skipped)
        assertEquals(24, data?.failed)
        assertEquals(86, data?.total)
    }

    @Test
    fun getForm() {
        val form = dataType.getForm(null)
        assertNotNull(form.getField("passed"))
        assertNotNull(form.getField("skipped"))
        assertNotNull(form.getField("failed"))
    }

    @Test
    fun fromFormNull() {
        assertNull(dataType.fromForm(null))
    }

    @Test
    fun fromForm() {
        val data = dataType.fromForm(
                jsonOf(
                        "passed" to 12,
                        "skipped" to 50,
                        "failed" to 24
                )
        )
        assertEquals(12, data?.passed)
        assertEquals(50, data?.skipped)
        assertEquals(24, data?.failed)
    }

    @Test(expected = ValidationRunDataInputException::class)
    fun validateDataNull() {
        dataType.validateData(TestSummaryValidationConfig(false), null)
    }

    @Test(expected = ValidationRunDataInputException::class)
    fun validateDataPassedNegative() {
        dataType.validateData(TestSummaryValidationConfig(false), TestSummaryValidationData(-1, 0, 0))
    }

    @Test(expected = ValidationRunDataInputException::class)
    fun validateDataSkippedNegative() {
        dataType.validateData(TestSummaryValidationConfig(false), TestSummaryValidationData(0, -1, 0))
    }

    @Test(expected = ValidationRunDataInputException::class)
    fun validateDataFailedNegative() {
        dataType.validateData(TestSummaryValidationConfig(false), TestSummaryValidationData(0, 0, -1))
    }

    @Test
    fun computeStatusTests() {
        ValidationRunStatusID.STATUS_PASSED with config(warningIfSkipped = false) and test()
        ValidationRunStatusID.STATUS_PASSED with config(warningIfSkipped = false) and test(passed = 10)
        ValidationRunStatusID.STATUS_PASSED with config(warningIfSkipped = false) and test(passed = 10, skipped = 1)
        ValidationRunStatusID.STATUS_WARNING with config(warningIfSkipped = true) and test(passed = 10, skipped = 1)
        ValidationRunStatusID.STATUS_FAILED with config(warningIfSkipped = true) and test(passed = 10, skipped = 1, failed = 1)
    }

    private infix fun ValidationRunStatusID.with(config: TestSummaryValidationConfig) =
            ComputeStatusTest(this, config)

    private inner class ComputeStatusTest(
            private val statusID: ValidationRunStatusID,
            private val config: TestSummaryValidationConfig
    ) {
        infix fun and(data: TestSummaryValidationData) {
            val computedStatus = dataType.computeStatus(config, data)
            assertEquals(
                    statusID.id,
                    computedStatus?.id,
                    "warningIfSkipped=${config.warningIfSkipped},data={$data},expected=${statusID.id},actual=${computedStatus?.id}"
            )
        }
    }

    private fun config(warningIfSkipped: Boolean) =
            TestSummaryValidationConfig(warningIfSkipped)

    private fun test(passed: Int = 0, skipped: Int = 0, failed: Int = 0) = TestSummaryValidationData(
            passed = passed,
            skipped = skipped,
            failed = failed
    )

}