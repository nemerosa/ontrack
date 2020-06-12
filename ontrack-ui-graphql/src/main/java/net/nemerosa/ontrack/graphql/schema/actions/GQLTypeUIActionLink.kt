package net.nemerosa.ontrack.graphql.schema.actions

import graphql.Scalars
import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.nullableType
import org.springframework.stereotype.Component

@Component
class GQLTypeUIActionLink : GQLType {

    override fun createType(cache: GQLTypeCache?): GraphQLObjectType =
            GraphQLObjectType.newObject()
                    .name(typeName)
                    .description("""An `ActionLink` refers to a HTTP end point,
                         having an uri, a HTTP method (like `PUT`
                         or `POST`) and a type.
                         The  type identifies the type of action,
                         like "download", "create", "update", "delete", etc.""".trimMargin())
                    .field {
                        it.name(UIActionLink<*>::type.name)
                                .description("Link type")
                                .type(GraphQLString)
                                .dataFetcher { env ->
                                    env.getSource<UIActionLinkContext<*>>().link.type
                                }
                    }
                    .field {
                        it.name(UIActionLink<*>::description.name)
                                .description("Link description")
                                .type(GraphQLString)
                                .dataFetcher { env ->
                                    env.getSource<UIActionLinkContext<*>>().link.description
                                }
                    }
                    .field {
                        it.name(UIActionLinkContext<*>::enabled.name)
                                .description("Is this end point enabled, according to authorizations and state.")
                                .type(nullableType(Scalars.GraphQLBoolean, false))
                                .dataFetcher { env ->
                                    env.getSource<UIActionLinkContext<*>>().enabled
                                }
                    }
                    .field {
                        it.name(UIActionLink<*>::uri.name)
                                .description("End point URI")
                                .type(Scalars.GraphQLString)
                                .dataFetcher { env ->
                                    env.getSource<UIActionLinkContext<*>>().uri?.toString()
                                }
                    }
                    .build()

    override fun getTypeName(): String = UIActionLink::class.java.simpleName

}

class UIActionLinkContext<T : Any>(
        val link: UIActionLink<T>,
        val target: T?
) {
    val uri by lazy {
        target?.let { link.uri(target) }
    }
    val enabled: Boolean get() = uri != null
}