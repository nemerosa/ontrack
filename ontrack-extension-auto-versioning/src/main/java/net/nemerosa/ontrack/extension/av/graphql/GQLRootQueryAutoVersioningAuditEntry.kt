package net.nemerosa.ontrack.extension.av.graphql

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditQueryFilter
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditQueryService
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.stringArgument
import org.springframework.stereotype.Component

@Component
class GQLRootQueryAutoVersioningAuditEntry(
    private val gqlTypeAutoVersioningAuditEntry: GQLTypeAutoVersioningAuditEntry,
    private val autoVersioningAuditQueryService: AutoVersioningAuditQueryService,
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("autoVersioningAuditEntry")
            .description("Gets an audit entry using its UUID")
            .type(gqlTypeAutoVersioningAuditEntry.typeRef)
            .argument(stringArgument("uuid", "UUID of the entry", nullable = false))
            .dataFetcher { env ->
                val uuid: String = env.getArgument("uuid")!!
                autoVersioningAuditQueryService.findByFilter(
                    filter = AutoVersioningAuditQueryFilter(uuid = uuid)
                ).firstOrNull()
            }
            .build()
}