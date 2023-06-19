package net.nemerosa.ontrack.extension.av.graphql

import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditEntry
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditQueryFilter
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditQueryService
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.pagination.GQLPaginatedListFactory
import org.springframework.stereotype.Component

@Component
class GQLRootQueryAutoVersioningAuditEntries(
    private val paginatedListFactory: GQLPaginatedListFactory,
    private val gqlinputAutoVersioningAuditQueryFilter: GQLInputAutoVersioningAuditQueryFilter,
    private val gqlTypeAutoVersioningAuditEntry: GQLTypeAutoVersioningAuditEntry,
    private val autoVersioningAuditQueryService: AutoVersioningAuditQueryService,
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        paginatedListFactory.createPaginatedField<Any?, AutoVersioningAuditEntry>(
            cache = GQLTypeCache(),
            fieldName = "autoVersioningAuditEntries",
            fieldDescription = "List of audit entries for auto versioning processing orders",
            itemType = gqlTypeAutoVersioningAuditEntry.typeName,
            itemListCounter = { env, _ ->
                val filterInput = env.getArgument<Any?>("filter")
                val filter = filterInput?.let { gqlinputAutoVersioningAuditQueryFilter.convert(it) }
                    ?: AutoVersioningAuditQueryFilter()
                autoVersioningAuditQueryService.countByFilter(filter)
            },
            itemListProvider = { env, _, offset, count ->
                val filterInput = env.getArgument<Any?>("filter")
                val filter = (filterInput?.let { gqlinputAutoVersioningAuditQueryFilter.convert(it) }
                    ?: AutoVersioningAuditQueryFilter())
                    .withOffset(offset)
                    .withCount(count)
                autoVersioningAuditQueryService.findByFilter(filter)
            },
            arguments = listOf(
                GraphQLArgument.newArgument()
                    .name("filter")
                    .description("Filter on the auto versioning entries")
                    .type(gqlinputAutoVersioningAuditQueryFilter.typeRef)
                    .build()
            )
        )
}