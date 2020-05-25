package net.nemerosa.ontrack.extension.indicators.model

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class IndicatorComplianceTest {

    @Test
    fun checks() {
        assertFailsWith<IllegalStateException> { IndicatorCompliance(-1) }
        assertFailsWith<IllegalStateException> { IndicatorCompliance(101) }
    }

    @Test
    fun comparisons() {
        assertEquals(IndicatorCompliance.HIGHEST, IndicatorCompliance(100))
        assertTrue(IndicatorCompliance.HIGHEST > IndicatorCompliance.MEDIUM)
        assertTrue(IndicatorCompliance.MEDIUM > IndicatorCompliance.LOWEST)
        assertEquals(IndicatorCompliance.LOWEST, IndicatorCompliance(0))
    }

}