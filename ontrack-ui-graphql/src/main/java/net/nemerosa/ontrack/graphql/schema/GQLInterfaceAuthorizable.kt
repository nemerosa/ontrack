package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLTypeReference
import graphql.schema.TypeResolverProxy
import net.nemerosa.ontrack.graphql.support.listType
import org.springframework.stereotype.Component

@Component
class GQLInterfaceAuthorizable : GQLInterface {

    companion object {
        const val NAME = "Authorizable"

        fun authorizationFieldBuilder() =
            GraphQLFieldDefinition.newFieldDefinition()
                .name("authorizations")
                .description("Authorizations for this context")
                .type(listType(GQLTypeAuthorization.ref))
    }

    override fun getTypeRef() = GraphQLTypeReference(NAME)

    override fun createInterface(): GraphQLInterfaceType =
        GraphQLInterfaceType.newInterface()
            .name(NAME)
            .description("Any type which contains authorizations for itself")
            .field(authorizationFieldBuilder().build())
            .typeResolver(TypeResolverProxy())
            .build()
}