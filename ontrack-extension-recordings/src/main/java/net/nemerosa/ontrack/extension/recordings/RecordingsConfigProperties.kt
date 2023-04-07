package net.nemerosa.ontrack.extension.recordings

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.convert.DurationUnit
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.temporal.ChronoUnit

@Component
@ConfigurationProperties(prefix = RecordingsConfigProperties.PREFIX)
class RecordingsConfigProperties {

    /**
     * Properties per extension for the cleanup
     */
    val cleanup = mutableMapOf<String, CleanupProperties>()

    class CleanupProperties {

        @DurationUnit(ChronoUnit.DAYS)
        var retention: Duration = Duration.ofDays(10)

        @DurationUnit(ChronoUnit.DAYS)
        var cleanup: Duration = Duration.ofDays(10)

    }

    companion object {
        const val PREFIX = "ontrack.extension.recordings"
    }

}