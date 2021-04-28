package net.nemerosa.ontrack.extension.influxdb

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions
import org.springframework.stereotype.Component

@Component
class InfluxDBExtensionFeature : AbstractExtensionFeature(
        "influxdb",
        "InfluxDB",
        "Support for InfluxDB as storage",
        ExtensionFeatureOptions.DEFAULT.withGui(true)
)
