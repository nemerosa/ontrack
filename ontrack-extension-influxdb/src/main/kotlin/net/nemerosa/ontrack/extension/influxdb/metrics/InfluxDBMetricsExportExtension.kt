package net.nemerosa.ontrack.extension.influxdb.metrics

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.api.MetricsExportExtension
import net.nemerosa.ontrack.extension.influxdb.InfluxDBConnection
import net.nemerosa.ontrack.extension.influxdb.InfluxDBExtensionFeature
import net.nemerosa.ontrack.extension.influxdb.InfluxDBExtensionProperties
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.metrics.Metric
import org.influxdb.dto.Point
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class InfluxDBMetricsExportExtension(
    extensionFeature: InfluxDBExtensionFeature,
    private val influxDBConnection: InfluxDBConnection,
    private val influxDBExtensionProperties: InfluxDBExtensionProperties,
) : AbstractExtension(extensionFeature), MetricsExportExtension {

    override fun exportMetrics(
        metric: String,
        tags: Map<String, String>,
        fields: Map<String, *>,
        timestamp: LocalDateTime?,
    ) {
        batchExportMetrics(
            listOf(
                Metric(
                    metric = metric,
                    tags = tags,
                    fields = fields,
                    timestamp = timestamp ?: Time.now()
                )
            )
        )
    }

    override fun batchExportMetrics(metrics: Collection<Metric>) {
        influxDBConnection.safe {
            metrics.forEach { metric ->
                if (metric.fields.isNotEmpty()) {
                    write(
                        Point.measurement(influxDBExtensionProperties.getMeasurementName(metric.metric))
                            // Tags
                            .tag(metric.tags)
                            // Fields
                            .fields(metric.fields)
                            // OK
                            .time(Time.toEpochMillis(metric.timestamp), TimeUnit.MILLISECONDS)
                            .build()
                    )
                }
            }
            flush()
        }
    }
}