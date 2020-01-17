package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.job.JobCategory

object RestorationJobs {

    val RESTORATION_JOB_TYPE = JobCategory.CORE.getType("restoration").withName("Restoration job")

}