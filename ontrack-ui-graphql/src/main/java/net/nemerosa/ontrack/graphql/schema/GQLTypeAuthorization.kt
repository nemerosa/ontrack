package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.model.security.Authorization

/**
 * GraphQL type defined in `core.graphqlq`.
 */
object GQLTypeAuthorization {
    private val NAME: String = Authorization::class.java.simpleName
    val ref = GraphQLTypeReference(NAME)
}