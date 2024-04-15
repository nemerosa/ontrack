package net.nemerosa.ontrack.graphql.schema.jobs

import net.nemerosa.ontrack.graphql.schema.AbstractGQLEnum
import net.nemerosa.ontrack.job.JobState
import org.springframework.stereotype.Component

@Component
class GQLEnumJobState : AbstractGQLEnum<JobState>(
    type = JobState::class,
    values = JobState.values(),
    description = "Global state of a job",
)
