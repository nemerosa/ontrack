package net.nemerosa.ontrack.graphql.schema.jobs

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.*
import net.nemerosa.ontrack.job.JobStatus
import org.springframework.stereotype.Component

@Component
class GQLTypeJobStatus : GQLType {

    override fun getTypeName(): String = JobStatus::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description(getTypeDescription(JobStatus::class))
            .longField(JobStatus::id)
            .field(JobStatus::key)
            .field(JobStatus::schedule)
            .field(JobStatus::actualSchedule)
            .stringField(JobStatus::description)
            .booleanField(JobStatus::isRunning)
            .booleanField(JobStatus::isValid)
            .booleanField(JobStatus::isPaused)
            .booleanField(JobStatus::isDisabled)
            .field(JobStatus::progress)
            .stringField(JobStatus::progressText)
            .longField(JobStatus::runCount)
            .localDateTimeField(JobStatus::lastRunDate)
            .longField(JobStatus::lastRunDurationMs)
            .localDateTimeField(JobStatus::nextRunDate)
            .longField(JobStatus::lastErrorCount)
            .longField(JobStatus::lastTimeoutCount)
            .stringField(JobStatus::lastError)
            .field(JobStatus::state)
            .booleanField(JobStatus::isError)
            .booleanField(JobStatus::isTimeout)
            .booleanFieldFunction<JobStatus>(name = "canRun", description = "Checks if the job can be run") {
                it.canRun()
            }
            .booleanFieldFunction<JobStatus>(name = "canPause", description = "Checks if the job can be paused") {
                it.canPause()
            }
            .booleanFieldFunction<JobStatus>(name = "canResume", description = "Checks if the job can be resumed") {
                it.canResume()
            }
            .booleanFieldFunction<JobStatus>(name = "canBeDeleted", description = "Checks if the job can be deleted") {
                it.canBeDeleted()
            }
            .booleanFieldFunction<JobStatus>(name = "canBeStopped", description = "Checks if the job can be stopped") {
                it.canBeDeleted()
            }
            .build()
}