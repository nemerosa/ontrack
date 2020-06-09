package net.nemerosa.ontrack.graphql.schema.authorizations

import graphql.schema.GraphQLFieldDefinition
import kotlin.reflect.KClass

interface AuthorizationsService {

    fun <T : Any> authorizationsField(type: KClass<T>): GraphQLFieldDefinition?

}