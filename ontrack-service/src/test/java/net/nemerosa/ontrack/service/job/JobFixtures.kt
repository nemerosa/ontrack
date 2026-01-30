package net.nemerosa.ontrack.service.job

import net.nemerosa.ontrack.job.JobCategory
import net.nemerosa.ontrack.job.JobKey
import net.nemerosa.ontrack.job.JobType
import net.nemerosa.ontrack.test.TestUtils.uid

object JobFixtures {

    fun jobKey() = JobKey(
        type = jobType(),
        id = uid("jk-"),
    )

    fun jobType() = JobType(
        category = jobCategory(),
        key = uid("jt-"),
        name = uid("JT "),
    )

    fun jobCategory() = JobCategory(
        key = uid("jc-"),
        name = uid("JC "),
    )

}