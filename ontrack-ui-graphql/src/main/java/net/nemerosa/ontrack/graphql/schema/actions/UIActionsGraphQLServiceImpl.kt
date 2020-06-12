package net.nemerosa.ontrack.graphql.schema.actions

import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class UIActionsGraphQLServiceImpl(
        private val uiAction: GQLTypeUIAction,
        actionsProviders: List<UIActionsProvider<*>>
) : UIActionsGraphQLService {

    /**
     * Index of action providers per target type
     */
    private val actionsIndex = actionsProviders.groupBy { it.targetType }

    override fun <T : Any> actionsField(type: KClass<T>): GraphQLFieldDefinition? {
        // Collects the actions providers for this type
        val actionsProviders = actionsIndex[type] ?: return null
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
                gqlType.actionField(action)
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

    private fun <T : Any> GraphQLObjectType.Builder.actionField(action: UIAction<T>) {
        field { f ->
            f.name(action.name)
                    .description(action.description)
                    .type(uiAction.typeRef)
                    .dataFetcher { env ->
                        val t: T? = env.getSource()
                        UIActionContext(action, t)
                    }
        }
    }

}