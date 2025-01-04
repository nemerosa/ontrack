package net.nemerosa.ontrack.extension.dm.tse

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIName
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.convert.DurationUnit
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.temporal.ChronoUnit

@Component
@ConfigurationProperties(prefix = "ontrack.extension.delivery-metrics.tse")
@APIName("Time since event metrics configuration")
@APIDescription("""Configuration of the export of the metrics of the "Time since events" (TSE).""")
class TimeSinceEventConfigurationProperties {
    @APIDescription("""Is the "time since event" metric enabled?""")
    var enabled: Boolean = true

    @DurationUnit(ChronoUnit.MINUTES)
    @APIDescription("""Interval between two scans for "time since events" (expressed by default in minutes)""")
    var interval: Duration = Duration.ofMinutes(30)
}
