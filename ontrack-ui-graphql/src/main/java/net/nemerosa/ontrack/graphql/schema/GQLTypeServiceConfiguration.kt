package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.support.GQLScalarJSON
import org.springframework.stereotype.Component

/**
 * GraphQL type for [net.nemerosa.ontrack.model.structure.ServiceConfiguration].
 */
@Component
class GQLTypeServiceConfiguration : GQLType {

    override fun getTypeName() = "ServiceConfiguration"

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
            GraphQLObjectType.newObject()
                    .name(typeName)
                    .description("Service configuration or data associated with an ID")
                    // ID
                    .field {
                        it.name("id")
                                .description("ID of the service configuration")
                                .type(GraphQLString)
                    }
                    // Data
                    .field {
                        it.name("data")
                                .description("Data for the service configuration")
                                .type(GQLScalarJSON.INSTANCE)
                    }
                    // OK
                    .build()
}