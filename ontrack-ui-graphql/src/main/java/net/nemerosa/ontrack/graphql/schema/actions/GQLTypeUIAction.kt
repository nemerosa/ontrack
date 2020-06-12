package net.nemerosa.ontrack.graphql.schema.actions

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.listField
import net.nemerosa.ontrack.graphql.support.objectField
import org.springframework.stereotype.Component

@Component
class GQLTypeUIAction(
        private val uiActionLink: GQLTypeUIActionLink,
        private val uiActionMutation: GQLTypeUIActionMutation
) : GQLType {

    override fun createType(cache: GQLTypeCache?): GraphQLObjectType =
            GraphQLObjectType.newObject()
                    .name(typeName)
                    .description("""An ""Action"" contains the information
                        about what's possible to do against the
                        encapsulating object.""".trimMargin())
                    .field {
                        it.name(UIAction<*>::name.name)
                                .description("Action name")
                                .type(GraphQLString)
                                .dataFetcher { env ->
                                    env.getSource<UIActionContext<*>>().action.name
                                }
                    }
                    .field {
                        it.name(UIAction<*>::description.name)
                                .description("Action description")
                                .type(GraphQLString)
                                .dataFetcher { env ->
                                    env.getSource<UIActionContext<*>>().action.description
                                }
                    }
                    .field(
                            listField(
                                    uiActionLink.typeRef,
                                    UIAction<*>::links.name,
                                    "Links of links for this action.",
                                    nullable = false
                            ) { env ->
                                env.getSource<UIActionContext<*>>().links
                            }
                    )
                    .field(
                            objectField(
                                    uiActionMutation.typeRef,
                                    UIAction<*>::mutation.name,
                                    "Mutation for this action",
                                    nullable = true
                            ) { env ->
                                env.getSource<UIActionContext<*>>().mutation
                            }
                    )
                    .build()

    override fun getTypeName(): String = UIAction::class.java.simpleName

}

class UIActionContext<T : Any>(
        val action: UIAction<T>,
        val target: T?
) {
    val links = action.links.map {
        UIActionLinkContext(it, target)
    }
    val mutation = action.mutation?.let {
        UIActionMutationContext(it, target)
    }
}
