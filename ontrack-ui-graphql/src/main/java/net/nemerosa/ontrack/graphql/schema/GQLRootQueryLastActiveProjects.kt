package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.GraphQLInt
import graphql.language.IntValue
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component
import java.math.BigInteger

@Component
class GQLRootQueryLastActiveProjects(
    private val structureService: StructureService,
): GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("lastActiveProjects")
            .description("Returns the list of last active projects")
            .argument {
                it.name(ARG_COUNT)
                    .description("Maximum number of projects to return")
                    .type(GraphQLInt)
                    .defaultValueLiteral(IntValue(BigInteger.TEN))
            }
            .type(listType(GQLTypeProject.PROJECT))
            .dataFetcher { env ->
                val count = env.getArgumentOrDefault(ARG_COUNT, 10)
                structureService.lastActiveProjects(count)
            }
            .build()

    companion object {
        const val ARG_COUNT = "count"
    }
}