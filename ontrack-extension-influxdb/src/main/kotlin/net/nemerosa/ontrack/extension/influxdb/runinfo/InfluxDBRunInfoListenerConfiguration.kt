package net.nemerosa.ontrack.extension.influxdb.runinfo

import net.nemerosa.ontrack.extension.influxdb.INFLUXDB_EXTENSION_PROPERTIES_PREFIX
import net.nemerosa.ontrack.extension.influxdb.InfluxDBExtensionConfiguration
import org.influxdb.InfluxDB
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@AutoConfigureAfter(InfluxDBExtensionConfiguration::class)
class InfluxDBRunInfoListenerConfiguration {

    @Bean
    @ConditionalOnBean(InfluxDB::class)
    @ConditionalOnProperty(prefix = INFLUXDB_EXTENSION_PROPERTIES_PREFIX, name = ["run-info"], havingValue = "true", matchIfMissing = true)
    fun influxDBRunInfoListener(influxDB: InfluxDB) = InfluxDBRunInfoListener(influxDB)

}