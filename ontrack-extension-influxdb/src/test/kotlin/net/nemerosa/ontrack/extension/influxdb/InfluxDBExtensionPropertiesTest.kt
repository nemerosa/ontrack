package net.nemerosa.ontrack.extension.influxdb

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class InfluxDBExtensionPropertiesTest {

    @Test
    fun `Default prefix`() {
        val properties = InfluxDBExtensionProperties()
        assertEquals("ontrack_validation_data", properties.getMeasurementName("validation_data"))
        assertEquals("ontrack_metric", properties.getMeasurementName("ontrack_metric"))
    }

    @Test
    fun `Prefix with ontrack`() {
        val properties = InfluxDBExtensionProperties()
        properties.prefix = "ontrack_acceptance"
        assertEquals("ontrack_acceptance_validation_data", properties.getMeasurementName("validation_data"))
        assertEquals("ontrack_acceptance_metric", properties.getMeasurementName("ontrack_metric"))
    }

    @Test
    fun `Prefix without ontrack`() {
        val properties = InfluxDBExtensionProperties()
        properties.prefix = "instance"
        assertEquals("instance_validation_data", properties.getMeasurementName("validation_data"))
        assertEquals("instance_metric", properties.getMeasurementName("ontrack_metric"))
    }

}