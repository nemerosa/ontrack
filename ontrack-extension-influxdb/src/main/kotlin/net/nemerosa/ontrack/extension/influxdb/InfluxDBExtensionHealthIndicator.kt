package net.nemerosa.ontrack.extension.influxdb

import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator

class InfluxDBExtensionHealthIndicator(
    private val influxDBConnection: InfluxDBConnection
) : HealthIndicator {

    override fun health(): Health {
        val ok: Boolean = try {
            influxDBConnection.isValid
        } catch (ex: Exception) {
            false
        }
        return if (ok) {
            Health.up().build()
        } else {
            Health.down().build()
        }
    }

}