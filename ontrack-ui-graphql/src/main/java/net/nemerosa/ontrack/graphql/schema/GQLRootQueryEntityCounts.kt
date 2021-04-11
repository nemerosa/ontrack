package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.GraphQLInt
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.model.structure.StatsService
import org.springframework.stereotype.Component

/**
 * Getting the count of entities
 */
@Component
class GQLRootQueryEntityCounts(
    private val gqlTypeEntityCounts: GQLTypeEntityCounts
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("entityCounts")
            .description("Collection of entity counts")
            .type(GraphQLNonNull(gqlTypeEntityCounts.typeRef))
            .dataFetcher { EntityCounts() }
            .build()
}

/**
 * Collection of entity counts
 */
@Component
class GQLTypeEntityCounts(
    private val statsService: StatsService
) : GQLType {

    override fun getTypeName(): String = EntityCounts::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Representation of the entity counts")
            .field {
                it.name("projects")
                    .description("Number of projects")
                    .type(GraphQLNonNull(GraphQLInt))
                    .dataFetcher { statsService.projectCount }
            }
            .build()

}

/**
 * Representation of the entity counts
 */
internal class EntityCounts