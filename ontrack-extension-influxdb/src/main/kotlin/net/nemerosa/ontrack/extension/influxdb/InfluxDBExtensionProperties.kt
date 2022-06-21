package net.nemerosa.ontrack.extension.influxdb

import org.influxdb.InfluxDB
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.convert.DurationUnit
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.temporal.ChronoUnit

const val INFLUXDB_EXTENSION_PROPERTIES_PREFIX = "ontrack.influxdb"

@ConfigurationProperties(prefix = INFLUXDB_EXTENSION_PROPERTIES_PREFIX)
@Component
class InfluxDBExtensionProperties {
    var enabled: Boolean = false
    var uri: String = "http://localhost:8086"
    var username: String = "root"
    var password: String = "root"
    var db: String = "ontrack"
    var create: Boolean = true
    var ssl = SSLProperties()
    var log = InfluxDB.LogLevel.NONE

    /**
     * Prefix to add before the metric name.
     *
     * For example, if prefix = `ontrack`
     *
     * * `validation_data` becomes `ontrack_validation_data`
     * * `ontrack_metric` becomes `ontrack_metric` (no change)
     *
     * For example, if prefix = `ontrack_acceptance`
     *
     * * `validation_data` becomes `ontrack_acceptance_validation_data`
     * * `ontrack_metric` becomes `ontrack_acceptance_metric`
     *
     * For example, if prefix = `instance`
     *
     * * `validation_data` becomes `instance_validation_data`
     * * `ontrack_metric` becomes `instance_metric`
     */
    var prefix: String = "ontrack"

    /**
     * Duration after which the connection to InfluxDB is checked for validity and renewed if necessary.
     */
    @DurationUnit(ChronoUnit.MINUTES)
    var validity: Duration = Duration.ofMinutes(15)

    class SSLProperties {
        var hostCheck: Boolean = true
    }

    fun getMeasurementName(name: String): String =
        if (name.startsWith("ontrack_")) {
            "${prefix}_${name.removePrefix("ontrack_")}"
        } else {
            "${prefix}_$name"
        }
}
