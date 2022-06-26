package net.nemerosa.ontrack.extension.av

import net.nemerosa.ontrack.job.JobCategory

object AutoVersioningJobs {

    val category: JobCategory = JobCategory
        .of("auto-versioning")
        .withName("Auto versioning")

}