package net.nemerosa.ontrack.extension.influxdb

import net.nemerosa.ontrack.extension.influxdb.metrics.InfluxDBMetricsExportExtension
import net.nemerosa.ontrack.extension.influxdb.runinfo.InfluxDBRunInfoListener
import net.nemerosa.ontrack.extension.influxdb.validation.data.InfluxDBValidationRunMetricsExtension
import net.nemerosa.ontrack.model.structure.ValidationDataTypeService
import org.influxdb.InfluxDB
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class InfluxDBExtensionConfiguration(
        private val influxDBExtensionProperties: InfluxDBExtensionProperties
) {

    @Bean
    @ConditionalOnProperty(prefix = INFLUXDB_EXTENSION_PROPERTIES_PREFIX, name = ["enabled"], havingValue = "true", matchIfMissing = false)
    fun influxDBConnection(): InfluxDBConnection = DefaultInfluxDBConnection(influxDBExtensionProperties)

    @Bean
    @ConditionalOnBean(InfluxDB::class)
    fun influxDBExtensionHealthIndicator(influxDBConnection: InfluxDBConnection) = InfluxDBExtensionHealthIndicator(influxDBConnection)

    @Bean
    @ConditionalOnBean(InfluxDB::class)
    @ConditionalOnProperty(prefix = INFLUXDB_EXTENSION_PROPERTIES_PREFIX, name = ["run-info"], havingValue = "true", matchIfMissing = true)
    fun influxDBMetricsExportExtension(
            influxDBExtensionFeature: InfluxDBExtensionFeature,
            influxDBConnection: InfluxDBConnection
    ) = InfluxDBMetricsExportExtension(
            influxDBExtensionFeature,
        influxDBConnection
    )

    @Bean
    @ConditionalOnBean(InfluxDB::class)
    @ConditionalOnProperty(prefix = INFLUXDB_EXTENSION_PROPERTIES_PREFIX, name = ["run-info"], havingValue = "true", matchIfMissing = true)
    fun influxDBRunInfoListener(influxDBConnection: InfluxDBConnection) = InfluxDBRunInfoListener(influxDBConnection)

    @Bean
    @ConditionalOnBean(InfluxDB::class)
    @ConditionalOnProperty(prefix = INFLUXDB_EXTENSION_PROPERTIES_PREFIX, name = ["validation-data"], havingValue = "true", matchIfMissing = true)
    fun influxDBValidationRunMetricsExtension(
            influxDBExtensionFeature: InfluxDBExtensionFeature,
            validationDataTypeService: ValidationDataTypeService,
            influxDBConnection: InfluxDBConnection
    ) = InfluxDBValidationRunMetricsExtension(influxDBExtensionFeature, validationDataTypeService, influxDBConnection)


}