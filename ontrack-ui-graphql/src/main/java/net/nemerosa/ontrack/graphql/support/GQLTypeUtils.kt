package net.nemerosa.ontrack.graphql.support

import graphql.schema.GraphQLTypeReference
import kotlin.reflect.KClass

/**
 * Given a Kotlin class, gets the corresponding GraphQL object type by using the class simple name.
 */
fun KClass<*>.toTypeRef() = GraphQLTypeReference(java.simpleName)
