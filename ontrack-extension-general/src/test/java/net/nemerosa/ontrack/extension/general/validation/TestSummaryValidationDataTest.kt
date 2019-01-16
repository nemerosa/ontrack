package net.nemerosa.ontrack.extension.general.validation

import org.junit.Test
import kotlin.test.assertEquals

class TestSummaryValidationDataTest {

    @Test
    fun `Total set to 0`() {
        val data = TestSummaryValidationData(0, 0, 0)
        assertEquals(0, data.total)
    }

    @Test
    fun `Total some failures`() {
        val data = TestSummaryValidationData(25, 0, 25)
        assertEquals(50, data.total)
    }

    @Test
    fun `Total considers skipped tests`() {
        val data = TestSummaryValidationData(25, 25, 0)
        assertEquals(50, data.total)
    }

}