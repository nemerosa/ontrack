package net.nemerosa.ontrack.extension.av.graphql

import graphql.Scalars
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditEntry
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.schema.GQLTypePromotionRun
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.graphql.support.toNotNull
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

@Component
class GQLTypeAutoVersioningAuditEntry(
    private val gqlTypeAutoVersioningOrder: GQLTypeAutoVersioningOrder,
    private val gqlTypeAutoVersioningAuditEntryState: GQLTypeAutoVersioningAuditEntryState,
    private val structureService: StructureService,
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
            // Upgrade branch
            .stringField(AutoVersioningAuditEntry::upgradeBranch)
            // Routing & queue
            .stringField(AutoVersioningAuditEntry::routing)
            .stringField(AutoVersioningAuditEntry::queue)
            // Promotion run which triggered the auto-versioning
            .field {
                it.name("promotionRun")
                    .description("Promotion which triggered the auto-versioning")
                    .type(GraphQLTypeReference(GQLTypePromotionRun.PROMOTION_RUN))
                    .dataFetcher { env ->
                        val entry: AutoVersioningAuditEntry = env.getSource()
                        val promotionRunId = entry.order.sourcePromotionRunId
                        promotionRunId?.let {
                            structureService.findPromotionRunByID(ID.of(it))
                        }
                    }
            }
            // OK
            .build()
}