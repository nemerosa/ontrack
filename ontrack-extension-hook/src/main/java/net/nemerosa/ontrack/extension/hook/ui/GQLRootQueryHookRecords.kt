package net.nemerosa.ontrack.extension.hook.ui

import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.hook.records.HookRecord
import net.nemerosa.ontrack.extension.hook.records.HookRecordQueryFilter
import net.nemerosa.ontrack.extension.hook.records.HookRecordQueryService
import net.nemerosa.ontrack.extension.hook.records.HookRecordState
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.enumArgument
import net.nemerosa.ontrack.graphql.support.pagination.GQLPaginatedListFactory
import net.nemerosa.ontrack.graphql.support.stringArgument
import net.nemerosa.ontrack.model.pagination.PaginatedList
import org.springframework.stereotype.Component

@Component
class GQLRootQueryHookRecords(
        private val gqlPaginatedListFactory: GQLPaginatedListFactory,
        private val gqlTypeHookRecord: GQLTypeHookRecord,
        private val hookRecordQueryService: HookRecordQueryService,
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
            gqlPaginatedListFactory.createPaginatedField<Any?, HookRecord>(
                    cache = GQLTypeCache(),
                    fieldName = "hookRecords",
                    fieldDescription = "Getting a paginated list of records received by the hooks.",
                    itemType = gqlTypeHookRecord,
                    itemPaginatedListProvider = { env, _, offset, size ->
                        getPaginatedList(env, offset, size)
                    },
                    arguments = listOf(
                            stringArgument(ARG_ID, "Record message ID"),
                            stringArgument(ARG_HOOK, "Hook end point"),
                            enumArgument<HookRecordState>(ARG_STATE, "Hook message state"),
                            stringArgument(ARG_TEXT, "Text in the payloads"),
                    )
            )

    private fun getPaginatedList(env: DataFetchingEnvironment, offset: Int, size: Int): PaginatedList<HookRecord> {
        val filter = HookRecordQueryFilter(
                id = env.getArgument(ARG_ID),
                hook = env.getArgument(ARG_HOOK),
                state = env.getArgument<String?>(ARG_STATE)?.let { HookRecordState.valueOf(it) },
                text = env.getArgument(ARG_TEXT),
        )
        return hookRecordQueryService.findByFilter(filter, offset, size)
    }

    companion object {
        const val ARG_ID = "id"
        const val ARG_HOOK = "hook"
        const val ARG_STATE = "state"
        const val ARG_TEXT = "text"
    }
}