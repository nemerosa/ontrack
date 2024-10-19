package net.nemerosa.ontrack.extensions.environments.ui

import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.extensions.environments.Slot
import net.nemerosa.ontrack.extensions.environments.service.SlotService
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeBuild
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GQLTypeSlot(
    private val slotService: SlotService,
) : GQLType {
    override fun getTypeName(): String = Slot::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Deployment slot into an environment")
            .stringField(Slot::id)
            .stringField(Slot::description)
            .field(Slot::project)
            .stringField(Slot::qualifier)
            .field(Slot::environment)
            // Last eligible build
            .field {
                it.name("eligibleBuild")
                    .description("Last eligible build for this slot")
                    .type(GraphQLTypeReference(GQLTypeBuild.BUILD))
                    .dataFetcher { env ->
                        val slot: Slot = env.getSource()
                        slotService.getEligibleBuilds(slot, count = 1).firstOrNull()
                    }
            }
            // OK
            .build()
}