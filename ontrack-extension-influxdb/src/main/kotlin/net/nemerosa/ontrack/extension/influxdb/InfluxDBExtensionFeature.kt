package net.nemerosa.ontrack.extension.influxdb

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature
import org.springframework.stereotype.Component

@Component
class InfluxDBExtensionFeature : AbstractExtensionFeature(
        "influxdb",
        "InfluxDB",
        "Support for InfluxDB as storage"
)
