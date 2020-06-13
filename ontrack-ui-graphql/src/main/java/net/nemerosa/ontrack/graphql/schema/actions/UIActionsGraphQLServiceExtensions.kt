package net.nemerosa.ontrack.graphql.schema.actions

import graphql.schema.GraphQLObjectType
import kotlin.reflect.KClass

fun <T : Any> GraphQLObjectType.Builder.actions(
        service: UIActionsGraphQLService,
        type: KClass<T>
): GraphQLObjectType.Builder =
        service.actionsField(type)
                // If field created, registers it
                ?.let {
                    field(it)
                }
        // If not, just returns the builder
                ?: this
