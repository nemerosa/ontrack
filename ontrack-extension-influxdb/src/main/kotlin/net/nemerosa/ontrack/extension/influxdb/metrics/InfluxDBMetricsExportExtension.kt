package net.nemerosa.ontrack.extension.influxdb.metrics

import net.nemerosa.ontrack.extension.api.MetricsExportExtension
import net.nemerosa.ontrack.extension.influxdb.InfluxDBExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import org.influxdb.InfluxDB
import org.influxdb.dto.Point
import java.util.concurrent.TimeUnit

class InfluxDBMetricsExportExtension(
        extensionFeature: InfluxDBExtensionFeature,
        private val influxDB: InfluxDB
) : AbstractExtension(extensionFeature), MetricsExportExtension {

    override fun exportMetrics(metric: String, tags: Map<String, String>, fields: Map<String, Double>) {
        if (fields.isNotEmpty()) {
            influxDB.write(
                    Point.measurement(metric)
                            // Tags
                            .tag(tags)
                            // Fields
                            .fields(fields)
                            // OK
                            .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
                            .build()
            )
            influxDB.flush()
        }
    }

}