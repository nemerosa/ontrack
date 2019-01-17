package net.nemerosa.ontrack.extension.general.validation

import net.nemerosa.ontrack.model.structure.ValidationRunStatusID
import org.junit.Test
import kotlin.test.assertEquals

class TestSummaryValidationConfigTest {

    @Test
    fun `Status failed when at least one failure`() {
        assertEquals(
                ValidationRunStatusID.FAILED,
                TestSummaryValidationConfig(false).computeStatus(
                        TestSummaryValidationData(100, 100, 1)
                )?.id
        )
    }

    @Test
    fun `Status warning when at least one skipped test and warning mode on`() {
        assertEquals(
                ValidationRunStatusID.WARNING,
                TestSummaryValidationConfig(true).computeStatus(
                        TestSummaryValidationData(100, 1, 0)
                )?.id
        )
    }

    @Test
    fun `Status passed when at least one skipped test and warning mode off`() {
        assertEquals(
                ValidationRunStatusID.PASSED,
                TestSummaryValidationConfig(false).computeStatus(
                        TestSummaryValidationData(100, 1, 0)
                )?.id
        )
    }

    @Test
    fun `Status passed when only passed tests`() {
        assertEquals(
                ValidationRunStatusID.PASSED,
                TestSummaryValidationConfig(true).computeStatus(
                        TestSummaryValidationData(100, 0, 0)
                )?.id
        )
    }

    @Test
    fun `Status passed when no test`() {
        assertEquals(
                ValidationRunStatusID.PASSED,
                TestSummaryValidationConfig(true).computeStatus(
                        TestSummaryValidationData(0, 0, 0)
                )?.id
        )
    }

}