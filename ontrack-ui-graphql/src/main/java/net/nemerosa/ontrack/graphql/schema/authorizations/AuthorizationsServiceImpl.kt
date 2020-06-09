package net.nemerosa.ontrack.graphql.schema.authorizations

import graphql.Scalars.GraphQLBoolean
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import org.springframework.stereotype.Service
import kotlin.reflect.KClass

@Service
class AuthorizationsServiceImpl(
        authorizationsProviders: List<Authorizations<*>>
) : AuthorizationsService {

    /**
     * Index of authorizations per type
     */
    private val authorizationsIndex = authorizationsProviders.groupBy { it.targetType }

    override fun <T : Any> authorizationsField(type: KClass<T>): GraphQLFieldDefinition? {
        // Collects the authorizations for this type
        val authorizationsList = authorizationsIndex[type] ?: return null
        // Creates a type to hold all authorizations
        val name = type.java.simpleName
        val typeName = "${name}Authorizations"
        val typeDescription = "Authorizations for a $name"
        val gqlType = GraphQLObjectType.newObject()
                .name(typeName)
                .description(typeDescription)
        // TODO One field per possible authorization (and associated fetcher)
        authorizationsList.forEach { authorizations ->
            authorizations.authorizations.forEach { authorization ->
                gqlType.authorizationField(authorization)
            }
        }
        // Authorizations field
        return GraphQLFieldDefinition.newFieldDefinition()
                .name("authorizations")
                .description(typeDescription)
                .type(gqlType.build())
                .dataFetcher { it.getSource<T>() }
                .build()
    }

    private fun <T : Any> GraphQLObjectType.Builder.authorizationField(authorization: Authorization<T>) {
        field { f ->
            f.name(authorization.name)
                    .description(authorization.description)
                    .type(GraphQLNonNull(GraphQLBoolean))
                    .dataFetcher { env ->
                        val t = env.getSource<T>()
                        authorization.check(t)
                    }
        }
    }
}
