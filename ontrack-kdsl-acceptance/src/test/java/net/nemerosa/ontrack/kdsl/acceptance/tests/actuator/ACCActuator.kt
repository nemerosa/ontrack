package net.nemerosa.ontrack.kdsl.acceptance.tests.actuator

import net.nemerosa.ontrack.kdsl.acceptance.tests.AbstractACCTestSupport
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class ACCActuator : AbstractACCTestSupport() {

    @Test
    fun `Access to Prometheus metrics`() {
        val metrics = getMetrics()
        assertTrue(metrics.list.isNotEmpty(), "Metrics are available")
    }

}