package net.nemerosa.ontrack.extension.influxdb

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIName
import org.influxdb.InfluxDB
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.convert.DurationUnit
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.temporal.ChronoUnit

const val INFLUXDB_EXTENSION_PROPERTIES_PREFIX = "ontrack.influxdb"

@ConfigurationProperties(prefix = INFLUXDB_EXTENSION_PROPERTIES_PREFIX)
@Component
@APIName("InfluxDB configuration")
@APIDescription("Configuration of the connection to InfluxDB for the export of metrics.")
class InfluxDBExtensionProperties {
    @APIDescription("Enabling the export of metrics to InfluxDB")
    var enabled: Boolean = false
    @APIDescription("URL of the InfluxDB instance")
    var uri: String = "http://localhost:8086"
    @APIDescription("Username used to connect to InfluxDB")
    var username: String = "root"
    @APIDescription("Password used to connect to InfluxDB")
    var password: String = "root"
    @APIDescription("Name of the InfluxDB database where to send the metrics")
    var db: String = "ontrack"
    @APIDescription("If the database must be created automatically")
    var create: Boolean = true
    var ssl = SSLProperties()
    @APIDescription("Log level of the InfluxDB commands")
    var log = InfluxDB.LogLevel.NONE

    @APIDescription(
        """
            Prefix to add before the metric name.
            
            For example, if prefix = `ontrack`
            
            * `validation_data` becomes `ontrack_validation_data`
            * `ontrack_metric` becomes `ontrack_metric` (no change)
            
            For example, if prefix = `ontrack_acceptance`
            
            * `validation_data` becomes `ontrack_acceptance_validation_data`
            * `ontrack_metric` becomes `ontrack_acceptance_metric`
            
            For example, if prefix = `instance`
            
            * `validation_data` becomes `instance_validation_data`
            * `ontrack_metric` becomes `instance_metric`
        """
    )
    var prefix: String = "ontrack"

    @APIDescription("""Duration after which the connection to InfluxDB is checked for validity and renewed if necessary.""")
    @DurationUnit(ChronoUnit.MINUTES)
    var validity: Duration = Duration.ofMinutes(15)

    class SSLProperties {
        @APIDescription("If the SSL connection must be valid")
        var hostCheck: Boolean = true
    }

    fun getMeasurementName(name: String): String =
        if (name.startsWith("ontrack_")) {
            "${prefix}_${name.removePrefix("ontrack_")}"
        } else {
            "${prefix}_$name"
        }
}
