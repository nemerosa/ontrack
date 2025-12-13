package net.nemerosa.ontrack.graphql.schema.jobs

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.booleanArgument
import net.nemerosa.ontrack.graphql.support.enumArgument
import net.nemerosa.ontrack.graphql.support.pagination.GQLPaginatedListFactory
import net.nemerosa.ontrack.graphql.support.stringArgument
import net.nemerosa.ontrack.job.JobScheduler
import net.nemerosa.ontrack.job.JobState
import net.nemerosa.ontrack.job.JobStatus
import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.security.ApplicationManagement
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.stereotype.Component

@Component
class GQLRootQueryJobs(
    private val gqlPaginatedListFactory: GQLPaginatedListFactory,
    private val gqlTypeJobStatus: GQLTypeJobStatus,
    private val securityService: SecurityService,
    private val jobScheduler: JobScheduler,
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        gqlPaginatedListFactory.createRootPaginatedField<JobStatus>(
            cache = GQLTypeCache(),
            fieldName = "jobs",
            fieldDescription = "List of background jobs",
            itemType = gqlTypeJobStatus.typeName,
            arguments = listOf(
                stringArgument(ARG_DESCRIPTION, "Part of the description of the job"),
                enumArgument<JobState>(ARG_STATE, "State of the job"),
                booleanArgument(ARG_ERROR, "Jobs on error"),
                booleanArgument(ARG_TIMEOUT, "Jobs with timeout"),
                stringArgument(ARG_CATEGORY, "Category key"),
                stringArgument(ARG_TYPE, "Type key"),
            ),
            itemPaginatedListProvider = { env, offset, size ->
                securityService.checkGlobalFunction(ApplicationManagement::class.java)

                val description: String? = env.getArgument(ARG_DESCRIPTION)
                val state: JobState? = env.getArgument<String>(ARG_STATE)?.let { JobState.valueOf(it) }
                val error: Boolean = env.getArgument<Boolean>(ARG_ERROR) ?: false
                val timeout: Boolean = env.getArgument<Boolean>(ARG_TIMEOUT) ?: false
                val category: String? = env.getArgument(ARG_CATEGORY)
                val type: String? = env.getArgument(ARG_TYPE)

                val statuses = jobScheduler.jobStatuses
                    .filter { job ->
                        (description.isNullOrBlank() || job.description.contains(description, ignoreCase = true)) &&
                                (state == null || job.state == state) &&
                                (!error || job.isError) &&
                                (!timeout || job.isTimeout) &&
                                (category.isNullOrBlank() || job.key.type.category.key == category) &&
                                (type.isNullOrBlank() || job.key.type.key == type)
                    }
                    .sortedBy { it.id }

                PaginatedList.create(
                    items = statuses.drop(offset).take(size),
                    offset = offset,
                    pageSize = size,
                    total = statuses.size
                )
            }
        )

    companion object {
        const val ARG_DESCRIPTION = "description"
        const val ARG_STATE = "state"
        const val ARG_ERROR = "error"
        const val ARG_TIMEOUT = "timeout"
        const val ARG_CATEGORY = "category"
        const val ARG_TYPE = "type"
    }
}