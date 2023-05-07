package net.nemerosa.ontrack.extension.queue

import net.nemerosa.ontrack.job.JobCategory

object QueueJobs {

    val category: JobCategory = JobCategory
            .of("queue")
            .withName("Queue mgt")

}