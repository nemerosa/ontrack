package net.nemerosa.ontrack.graphql.support

import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.model.annotations.APIName
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

/**
 * Given a Kotlin class, gets the corresponding GraphQL object type by using the class simple name.
 */
fun KClass<*>.toTypeRef() =
    findAnnotation<APIName>()?.let {
        GraphQLTypeReference(it.value)
    } ?: GraphQLTypeReference(java.simpleName)
