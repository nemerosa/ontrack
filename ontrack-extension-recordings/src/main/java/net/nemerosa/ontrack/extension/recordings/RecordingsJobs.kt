package net.nemerosa.ontrack.extension.recordings

import net.nemerosa.ontrack.job.JobCategory

/**
 * Keys for the jobs
 */
object RecordingsJobs {

    val category: JobCategory = JobCategory.of("recordings").withName("Recordings")

}