package net.nemerosa.ontrack.extension.scm

import net.nemerosa.ontrack.common.api.APIDescription
import net.nemerosa.ontrack.job.Schedule
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.convert.DurationUnit
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.temporal.ChronoUnit

@ConfigurationProperties(prefix = "ontrack.config.extension.scm")
@Component
class SCMExtensionConfigProperties {

    @APIDescription("SCM catalog properties")
    var catalog = SCMCatalogConfigProperties()

    @APIDescription("SCM search properties")
    var search = SCMSearchConfigProperties()

    class SCMCatalogConfigProperties {
        @APIDescription("Enabling the SCM catalog")
        var enabled = false
    }

    class SCMSearchConfigProperties {
        @DurationUnit(ChronoUnit.HOURS)
        @APIDescription("Interval between two indexations, in hours.")
        var schedule: Duration = Duration.ofHours(1)

        @APIDescription("Enabling auto indexation")
        var scheduled: Boolean = true

        /**
         * Converting the [schedule] property to a job [Schedule]
         */
        fun toSchedule(): Schedule =
            if (scheduled) {
                Schedule.everyMinutes(schedule.toMinutes())
            } else {
                Schedule.NONE
            }
    }

}
