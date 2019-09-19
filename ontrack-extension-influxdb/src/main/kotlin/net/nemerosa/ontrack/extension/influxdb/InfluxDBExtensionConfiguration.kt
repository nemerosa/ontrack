package net.nemerosa.ontrack.extension.influxdb

import net.nemerosa.ontrack.extension.influxdb.runinfo.InfluxDBRunInfoListener
import net.nemerosa.ontrack.extension.influxdb.validation.data.InfluxDBValidationRunMetricsExtension
import net.nemerosa.ontrack.model.structure.ValidationDataTypeService
import okhttp3.OkHttpClient
import org.influxdb.BatchOptions
import org.influxdb.InfluxDB
import org.influxdb.InfluxDBFactory
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class InfluxDBExtensionConfiguration(
        private val influxDBExtensionProperties: InfluxDBExtensionProperties
) {

    private val logger = LoggerFactory.getLogger(InfluxDBExtensionConfiguration::class.java)

    @Bean
    @ConditionalOnMissingBean(InfluxDB::class)
    @ConditionalOnProperty(prefix = INFLUXDB_EXTENSION_PROPERTIES_PREFIX, name = ["enabled"], havingValue = "true", matchIfMissing = false)
    fun influxDB(): InfluxDB {
        logger.info("InfluxDB URI = ${influxDBExtensionProperties.uri}")
        logger.info("InfluxDB database = ${influxDBExtensionProperties.db}")

        var builder = OkHttpClient.Builder()
        if (!influxDBExtensionProperties.ssl.hostCheck) {
            builder = builder.hostnameVerifier { _, _ -> true }
        }

        val influxDB = InfluxDBFactory.connect(
                influxDBExtensionProperties.uri,
                influxDBExtensionProperties.username,
                influxDBExtensionProperties.password,
                builder
        )
        influxDB.setDatabase(influxDBExtensionProperties.db)
        if (influxDBExtensionProperties.create) {
            influxDB.createDatabase(influxDBExtensionProperties.db)
        }
        influxDB.enableBatch(BatchOptions.DEFAULTS)
        return influxDB
    }

    @Bean
    @ConditionalOnBean(InfluxDB::class)
    fun influxDBExtensionHealthIndicator(influxDB: InfluxDB) = InfluxDBExtensionHealthIndicator(influxDB)


    @Bean
    @ConditionalOnBean(InfluxDB::class)
    @ConditionalOnProperty(prefix = INFLUXDB_EXTENSION_PROPERTIES_PREFIX, name = ["run-info"], havingValue = "true", matchIfMissing = true)
    fun influxDBRunInfoListener(influxDB: InfluxDB) = InfluxDBRunInfoListener(influxDB)

    @Bean
    @ConditionalOnBean(InfluxDB::class)
    @ConditionalOnProperty(prefix = INFLUXDB_EXTENSION_PROPERTIES_PREFIX, name = ["validation-data"], havingValue = "true", matchIfMissing = true)
    fun influxDBValidationRunMetricsExtension(
            influxDBExtensionFeature: InfluxDBExtensionFeature,
            validationDataTypeService: ValidationDataTypeService,
            influxDB: InfluxDB
    ) = InfluxDBValidationRunMetricsExtension(influxDBExtensionFeature, validationDataTypeService, influxDB)


}