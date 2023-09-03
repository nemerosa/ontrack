package net.nemerosa.ontrack.graphql.schema.authorizations

import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.graphql.schema.GQLInterfaceAuthorizable
import net.nemerosa.ontrack.model.security.AuthorizationService
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class GQLInterfaceAuthorizableService(
    private val authorizationService: AuthorizationService,
) {

    fun <T : Any> apply(
        builder: GraphQLObjectType.Builder,
        kClass: KClass<T>
    ) {
        // Applies the interface
        builder.withInterface(GraphQLTypeReference(GQLInterfaceAuthorizable.NAME))
        // Applies the `authorizations` field
        builder.field(
            GQLInterfaceAuthorizable.authorizationFieldBuilder()
                .dataFetcher { env ->
                    // Gets the current context
                    val context = env.getSource<T>()
                    // Gets the authorizations for this context
                    authorizationService.getAuthorizations(context)
                }
                .build()
        )
    }
}