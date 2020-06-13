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
                         like "download", "form", "upload", etc.""".trimMargin())
                    .field {
                        it.name("description")
                                .description("Link description")
                                .type(nullableType(GraphQLString, false))
                                .dataFetcher { env ->
                                    env.getSource<UIActionLinkContext<*>>().link.description
                                }
                    }
                    .field {
                        it.name("method")
                                .description("HTTP method to use")
                                .type(nullableType(GraphQLString, false))
                                .dataFetcher { env ->
                                    env.getSource<UIActionLinkContext<*>>().link.method.name
                                }
                    }
                    .field {
                        it.name("enabled")
                                .description("Is this end point enabled, according to authorizations and state.")
                                .type(nullableType(Scalars.GraphQLBoolean, false))
                                .dataFetcher { env ->
                                    env.getSource<UIActionLinkContext<*>>().enabled
                                }
                    }
                    .field {
                        it.name("uri")
                                .description("End point URI")
                                .type(GraphQLString)
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