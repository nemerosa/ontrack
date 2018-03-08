package net.nemerosa.ontrack.job.support

import net.nemerosa.ontrack.common.BaseException
import net.nemerosa.ontrack.job.JobKey

class JobNotScheduledException(key: JobKey) : BaseException("Job with key $key is not scheduled.")
