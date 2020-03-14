package net.nemerosa.ontrack.extension.git

import net.nemerosa.ontrack.job.Schedule
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.convert.DurationUnit
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.temporal.ChronoUnit

@Component
@ConfigurationProperties(prefix = GitSearchConfigProperties.GIT_SEARCH_PROPERTY_PREFIX)
class GitSearchConfigProperties {

    companion object {
        /**
         * Prefix for Git search configuration properties
         */
        const val GIT_SEARCH_PROPERTY_PREFIX = "${OntrackConfigProperties.SEARCH_PROPERTY}.git"
    }

    /**
     * Commit search configuration properties
     */
    var commits = GitCommitSearchConfigProperties()

    /**
     * Commit search configuration properties
     */
    class GitCommitSearchConfigProperties {
        /**
         * Interval between two indexations, in minutes
         */
        @DurationUnit(ChronoUnit.MINUTES)
        var schedule: Duration = Duration.ofHours(1)

        /**
         * Enabling auto indexation
         */
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