package net.nemerosa.ontrack.extension.influxdb

import org.influxdb.InfluxDB
import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator

class InfluxDBExtensionHealthIndicator(
        private val influxDB: InfluxDB
) : HealthIndicator {

    override fun health(): Health {
        val ok: Boolean = try {
            val pong = influxDB.ping()
            pong != null && pong.isGood
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