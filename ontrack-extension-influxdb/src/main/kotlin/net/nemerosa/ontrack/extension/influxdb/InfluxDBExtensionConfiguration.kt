package net.nemerosa.ontrack.extension.influxdb

import net.nemerosa.ontrack.extension.influxdb.metrics.InfluxDBMetricsExportExtension
import net.nemerosa.ontrack.extension.influxdb.runinfo.InfluxDBRunInfoListener
import net.nemerosa.ontrack.extension.influxdb.validation.data.InfluxDBValidationRunMetricsExtension
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.ValidationDataTypeService
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class InfluxDBExtensionConfiguration(
    private val influxDBExtensionProperties: InfluxDBExtensionProperties
) {

    @Bean
    @ConditionalOnProperty(prefix = INFLUXDB_EXTENSION_PROPERTIES_PREFIX,
        name = ["enabled"],
        havingValue = "true",
        matchIfMissing = false)
    fun influxDBConnection(securityService: SecurityService): InfluxDBConnection =
        DefaultInfluxDBConnection(influxDBExtensionProperties, securityService)

    @Bean
    @ConditionalOnBean(InfluxDBConnection::class)
    fun influxDBExtensionHealthIndicator(influxDBConnection: InfluxDBConnection) =
        InfluxDBExtensionHealthIndicator(influxDBConnection)

    @Bean
    @ConditionalOnBean(InfluxDBConnection::class)
    fun influxDBMetricsExportExtension(
        influxDBExtensionFeature: InfluxDBExtensionFeature,
        influxDBConnection: InfluxDBConnection
    ) = InfluxDBMetricsExportExtension(
        influxDBExtensionFeature,
        influxDBConnection
    )

    @Bean
    @ConditionalOnBean(InfluxDBConnection::class)
    fun influxDBRunInfoListener(influxDBConnection: InfluxDBConnection) = InfluxDBRunInfoListener(influxDBConnection)

    @Bean
    @ConditionalOnBean(InfluxDBConnection::class)
    fun influxDBValidationRunMetricsExtension(
        influxDBExtensionFeature: InfluxDBExtensionFeature,
        validationDataTypeService: ValidationDataTypeService,
        influxDBConnection: InfluxDBConnection
    ) = InfluxDBValidationRunMetricsExtension(influxDBExtensionFeature, validationDataTypeService, influxDBConnection)

    @Bean
    @ConditionalOnBean(InfluxDBConnection::class)
    fun influxDBExtensionActuatorEndPoint(
        influxDBConnection: InfluxDBConnection,
        securityService: SecurityService
    ) =
        InfluxDBExtensionActuatorEndPoint(
            influxDBConnection,
            securityService
        )

}