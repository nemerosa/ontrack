package net.nemerosa.ontrack.graphql.schema.actions

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.support.nullableType
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class UIActionsGraphQLServiceImpl(
        actionsProviders: List<UIActionsProvider<*>>,
        private val uiActionLink: GQLTypeUIActionLink
) : UIActionsGraphQLService {

    /**
     * Index of action providers per target type
     */
    private val actionsIndex = actionsProviders.groupBy { it.targetType }

    override fun <T : Any> actionsField(type: KClass<T>): GraphQLFieldDefinition? {
        // Collects the actions providers for this type
        @Suppress("UNCHECKED_CAST")
        val actionsProviders: List<UIActionsProvider<T>> =
                (actionsIndex[type] ?: return null) as List<UIActionsProvider<T>>
        // Creates a type to hold all actions
        val name = type.java.simpleName
        val typeName = "${name}Actions"
        val typeDescription = "Actions for a $name"
        val gqlType = GraphQLObjectType.newObject()
                .name(typeName)
                .description(typeDescription)
        // One field per action (and associated fetcher)
        actionsProviders.forEach { actionsProvider ->
            actionsProvider.actions.forEach { action ->
                gqlType.actionField(type, action)
            }
        }
        // Authorizations field
        return GraphQLFieldDefinition.newFieldDefinition()
                .name("actions")
                .description(typeDescription)
                .type(gqlType.build())
                .dataFetcher { it.getSource<T>() }
                .build()
    }

    private fun <T : Any> GraphQLObjectType.Builder.actionField(type: KClass<T>, action: UIAction<T>) {
        field { f ->
            f.name(action.name)
                    .description(action.description)
                    .type(createUIActionType(type, action))
                    .dataFetcher { it.getSource<T?>() }
        }
    }

    private fun <T : Any> createUIActionType(type: KClass<T>, action: UIAction<T>): GraphQLObjectType =
            GraphQLObjectType.newObject()
                    .name("${type.java.simpleName}Action${action.name.capitalize()}")
                    .description(action.description)
                    // Description field
                    .field {
                        it.name("description")
                                .description("Description of the action")
                                .type(nullableType(GraphQLString, false))
                                .dataFetcher { action.description }
                    }
                    // Links field
                    .apply {
                        if (action.links.isNotEmpty()) {
                            field {
                                it.name("links")
                                        .description("Links attached to this action")
                                        .type(createUIActionLinksType(type, action))
                                        .dataFetcher { it.getSource<T?>() }
                            }
                        }
                    }
                    // Mutation field
                    .apply {
                        if (action.mutation != null) {
                            field {
                                it.name("mutation")
                                        .description("Mutation associated with this action")
                                        .type(GraphQLString)
                                        .dataFetcher { env ->
                                            val t: T? = env.getSource()
                                            if (t != null && action.mutation.enabled(t)) {
                                                // Name of the mutation
                                                action.mutation.name
                                            } else {
                                                null
                                            }
                                        }
                            }
                        }
                    }
                    // OK
                    .build()

    private fun <T : Any> createUIActionLinksType(type: KClass<T>, action: UIAction<T>): GraphQLObjectType =
            GraphQLObjectType.newObject()
                    .name("${type.java.simpleName}Action${action.name.capitalize()}Links")
                    .description("Links attached to the ${action.name} action on the ${type.simpleName} type.")
                    .apply {
                        action.links.forEach { link ->
                            field {
                                it.name(link.type)
                                        .description(link.description)
                                        .type(uiActionLink.typeRef)
                                        .dataFetcher { env ->
                                            val t: T? = env.getSource()
                                            UIActionLinkContext(link, t)
                                        }
                            }
                        }
                    }
                    .build()

}