package net.nemerosa.ontrack.job

/**
 * Association of a job and a schedule for a registration at startup.
 */
data class JobRegistration(
        val job: Job,
        val schedule: Schedule
) {

    fun withSchedule(schedule: Schedule) = JobRegistration(job, schedule)

    fun everyMinutes(minutes: Long): JobRegistration {
        return withSchedule(Schedule.everyMinutes(minutes))
    }

    companion object {
        @JvmStatic
        fun of(job: Job): JobRegistration {
            return JobRegistration(job, Schedule.NONE)
        }
    }
}
