package net.nemerosa.ontrack.kdsl.acceptance.tests.actuator

import net.nemerosa.ontrack.kdsl.acceptance.tests.AbstractACCTestSupport
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ACCActuator : AbstractACCTestSupport() {

    @Test
    fun `Access to Prometheus metrics`() {
        val metrics = getMetrics()
        assertTrue(metrics.list.isNotEmpty(), "Metrics are available")
        val record = metrics.list.first()
        val value = record.values.first()
        val tags = value.tags
        assertNotNull(
            tags.find { it.name == "application" && it.value == "ontrack" },
            "Tag application is present and is equal to `ontrack`"
        )
        assertNotNull(
            tags.find { it.name == "application_instance" && it.value == "ontrack" },
            "Tag application_instance is present and is equal to `ontrack`"
        )
    }

}