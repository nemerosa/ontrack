package net.nemerosa.ontrack.extension.influxdb

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

const val INFLUXDB_EXTENSION_PROPERTIES_PREFIX = "ontrack.influxdb"

/**
 * @property enabled To enable the extension
 */
@ConfigurationProperties(prefix = INFLUXDB_EXTENSION_PROPERTIES_PREFIX)
@Component
class InfluxDBExtensionProperties(
        var enabled: Boolean = false,
        var uri: String = "http://localhost:8086",
        var username: String = "root",
        var password: String = "root",
        var db: String = "ontrack",
        var create: Boolean = true
)
