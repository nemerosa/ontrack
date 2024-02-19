package net.nemerosa.ontrack.graphql.schema.actions

import graphql.schema.GraphQLFieldDefinition
import kotlin.reflect.KClass

@Deprecated("Will be removed in V5.")
interface UIActionsGraphQLService {

    fun <T : Any> actionsField(type: KClass<T>): GraphQLFieldDefinition?

}