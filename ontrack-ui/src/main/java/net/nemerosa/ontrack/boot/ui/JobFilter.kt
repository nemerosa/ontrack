package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.job.JobState
import net.nemerosa.ontrack.job.JobStatus
import org.apache.commons.lang3.StringUtils

data class JobFilter(
        val state: JobState? = null,
        val category: String? = null,
        val type: String? = null,
        val description: String? = null,
        val errorOnly: Boolean = false
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
