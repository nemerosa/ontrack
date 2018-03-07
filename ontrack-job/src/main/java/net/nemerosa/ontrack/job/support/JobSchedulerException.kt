package net.nemerosa.ontrack.job.support

import net.nemerosa.ontrack.common.BaseException
import net.nemerosa.ontrack.job.JobKey

class JobSchedulerException(ex: Exception, key: JobKey) : BaseException(ex, "Cannot schedule job: $key")
