package net.nemerosa.ontrack.extension.recordings

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIName
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.convert.DurationUnit
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.temporal.ChronoUnit

@Component
@ConfigurationProperties(prefix = RecordingsConfigProperties.PREFIX)
@APIName("Recordings configuration")
class RecordingsConfigProperties {

    /**
     * Properties per extension for the cleanup
     */
    val cleanup = mutableMapOf<String, CleanupProperties>()

    class CleanupProperties {

        @DurationUnit(ChronoUnit.DAYS)
        @APIDescription("How much time must the closed records be kept")
        var retention: Duration = Duration.ofDays(10)

        @DurationUnit(ChronoUnit.DAYS)
        @APIDescription("How much more time after the retention must all the records be kept")
        var cleanup: Duration = Duration.ofDays(10)

    }

    companion object {
        const val PREFIX = "ontrack.extension.recordings"
    }

}