package net.nemerosa.ontrack.graphql.schema.jobs

import net.nemerosa.ontrack.graphql.schema.AbstractGQLEnum
import net.nemerosa.ontrack.model.job.JobHistoryItemStatus
import org.springframework.stereotype.Component

@Component
class GQLEnumJobHistoryItemStatus: AbstractGQLEnum<JobHistoryItemStatus>(
    type = JobHistoryItemStatus::class,
    values = JobHistoryItemStatus.entries.toTypedArray(),
    description = "Status of a job history item"
)