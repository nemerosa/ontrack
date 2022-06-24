package net.nemerosa.ontrack.extension.dm.tse

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.convert.DurationUnit
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.temporal.ChronoUnit

@Component
@ConfigurationProperties(prefix = "ontrack.extension.delivery-metrics.tse")
class TimeSinceEventConfigurationProperties {
    /**
     * Is the "time since event" metric enabled?
     */
    var enabled: Boolean = true

    /**
     * Interval between two scans for "time since events"
     */
    @DurationUnit(ChronoUnit.MINUTES)
    var interval: Duration = Duration.ofMinutes(30)
}
