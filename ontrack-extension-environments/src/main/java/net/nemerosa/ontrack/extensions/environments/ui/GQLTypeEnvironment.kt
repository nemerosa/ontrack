package net.nemerosa.ontrack.extensions.environments.ui

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extensions.environments.Environment
import net.nemerosa.ontrack.extensions.environments.service.SlotService
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.intField
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.graphql.support.stringListField
import org.springframework.stereotype.Component

@Component
class GQLTypeEnvironment(
    private val gqlTypeSlot: GQLTypeSlot,
    private val slotService: SlotService,
) : GQLType {

    override fun getTypeName(): String = Environment::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Description of an environment")
            .stringField(Environment::id)
            .stringField(Environment::name)
            .intField(Environment::order)
            .stringField(Environment::description)
            .stringListField(Environment::tags)
            .field {
                it.name("slots")
                    .description("List of slots for this environment")
                    .type(listType(gqlTypeSlot.typeRef))
                    .dataFetcher { env ->
                        val environment: Environment = env.getSource()
                        slotService.findSlotsByEnvironment(environment)
                    }
            }
            .build()
}