package net.nemerosa.ontrack.extension.hook

import net.nemerosa.ontrack.job.JobCategory

object HookJobs {

    val category: JobCategory = JobCategory
            .of("hook")
            .withName("Hook mgt")

}