package net.nemerosa.ontrack.extension.environments.ui

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.environments.Environment
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.*
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
            .booleanField(Environment::image)
            .field {
                it.name("slots")
                    .description("List of slots for this environment")
                    .argument(stringListArgument("projects", "List of project names", nullable = true))
                    .type(listType(gqlTypeSlot.typeRef))
                    .dataFetcher { env ->
                        val projectNames: List<String> = env.getArgument("projects") ?: emptyList()
                        val projectNamesIndex = projectNames.toSet()
                        val environment: Environment = env.getSource()
                        slotService.findSlotsByEnvironment(environment)
                            .filter {
                                projectNamesIndex.isEmpty() || it.project.name in projectNamesIndex
                            }
                    }
            }
            .build()
}