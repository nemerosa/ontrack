package net.nemerosa.ontrack.extension.av.graphql

import graphql.Scalars
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditEntry
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.graphql.support.toNotNull
import org.springframework.stereotype.Component

@Component
class GQLTypeAutoVersioningAuditEntry(
    private val gqlTypeAutoVersioningOrder: GQLTypeAutoVersioningOrder,
    private val gqlTypeAutoVersioningAuditEntryState: GQLTypeAutoVersioningAuditEntryState,
) : GQLType {

    override fun getTypeName(): String = AutoVersioningAuditEntry::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Audit entry for an auto versioning processing order.")
            .field(AutoVersioningAuditEntry::order, gqlTypeAutoVersioningOrder, "Associated auto versioning order")
            // Audit list
            .field {
                it.name("audit")
                    .description("History of the different states")
                    .type(listType(gqlTypeAutoVersioningAuditEntryState.typeRef))
            }
            // Most recent state
            .field {
                it.name("mostRecentState")
                    .description("Most recent state")
                    .type(gqlTypeAutoVersioningAuditEntryState.typeRef.toNotNull())
            }
            // Running flag
            .field {
                it.name("running")
                    .description("Running state")
                    .type(Scalars.GraphQLBoolean)
            }
            // Duration
            .field {
                it.name("duration")
                    .description("Elapsed time between the creation of this process and its last state (in ms)")
                    .type(Scalars.GraphQLInt)
            }
            // Routing & queue
            .stringField(AutoVersioningAuditEntry::routing)
            .stringField(AutoVersioningAuditEntry::queue)
            // OK
            .build()
}