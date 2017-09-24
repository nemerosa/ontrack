package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.job.JobState
import net.nemerosa.ontrack.job.JobStatus
import org.apache.commons.lang3.StringUtils

class JobFilter(
        var state: JobState? = null,
        var category: String? = null,
        var type: String? = null,
        var description: String? = null,
        var errorOnly: Boolean = false
) {
    fun filter(statuses: Collection<JobStatus>) =
            statuses.filter {
                (state == null || state == it.state) &&
                        (category == null || category == it.key.type.category.key) &&
                        (type == null || type == it.key.type.key) &&
                        (description == null || StringUtils.containsIgnoreCase(it.description, description)) &&
                        (!errorOnly || it.isError)
            }

}
