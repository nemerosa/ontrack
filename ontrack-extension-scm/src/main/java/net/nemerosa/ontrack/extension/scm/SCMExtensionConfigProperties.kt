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

    @APIDescription("Mock SCM properties")
    var mock = MockSCMConfigProperties()

    /**
     * Nested rather than a flat `mockEnabled`, so that the generated documentation publishes a
     * usable environment variable: the doc generator uppercases a camel-case field without
     * splitting it, and only the dots of a nested property become underscores.
     */
    class MockSCMConfigProperties {
        @APIDescription(
            "Enables the mock SCM, which keeps repositories, branches, commits and issues in memory " +
                    "instead of talking to a real SCM. Disabled by default; always enabled in the `dev` " +
                    "profile (the value shown opposite is the one the documentation build runs with). " +
                    "Enable it only on demonstration or test instances - never on an instance tracking " +
                    "real deliveries, where it would let a project claim an SCM that answers with " +
                    "whatever anyone posted to it. Its data lives on the bean and does not survive a " +
                    "restart."
        )
        var enabled: Boolean = false
    }

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
