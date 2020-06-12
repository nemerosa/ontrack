package net.nemerosa.ontrack.graphql.schema.actions

import graphql.Scalars
import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.nullableType
import org.springframework.stereotype.Component

@Component
class GQLTypeUIActionMutation : GQLType {

    override fun createType(cache: GQLTypeCache?): GraphQLObjectType =
            GraphQLObjectType.newObject()
                    .name(typeName)
                    .description("""An `ActionMutation` refers to a GraphQL mutation, 
    |                       which is enabled or not according to authorizations or state.""".trimMargin())
                    .field {
                        it.name(UIActionMutation<*>::name.name)
                                .description("Mutation name")
                                .type(GraphQLString)
                                .dataFetcher { env ->
                                    env.getSource<UIActionMutationContext<*>>().mutation.name
                                }
                    }
                    .field {
                        it.name(UIActionMutation<*>::enabled.name)
                                .description("Is this end point enabled, according to authorizations and state.")
                                .type(nullableType(Scalars.GraphQLBoolean, false))
                                .dataFetcher { env ->
                                    env.getSource<UIActionMutationContext<*>>().enabled
                                }
                    }
                    .build()

    override fun getTypeName(): String = UIActionMutation::class.java.simpleName

}

class UIActionMutationContext<T : Any>(
        val mutation: UIActionMutation<T>,
        val target: T?
) {
    val enabled: Boolean get() = target != null && mutation.enabled(target)
}