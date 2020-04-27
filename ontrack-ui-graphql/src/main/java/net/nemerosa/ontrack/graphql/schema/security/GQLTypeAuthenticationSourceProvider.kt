package net.nemerosa.ontrack.graphql.schema.security

import graphql.Scalars.GraphQLBoolean
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import net.nemerosa.ontrack.model.security.AuthenticationSource
import net.nemerosa.ontrack.model.security.AuthenticationSourceProvider
import org.springframework.stereotype.Component

@Component
class GQLTypeAuthenticationSourceProvider(
        private val authenticationSource: GQLTypeAuthenticationSource
) : GQLType {

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
            .name(typeName)
            .description("Authentication source provider")
            .field {
                it.name("enabled")
                        .description("Is this authentication source enabled?")
                        .type(GraphQLBoolean)
                        .dataFetcher { env ->
                            val provider = env.getSource<AuthenticationSourceProvider>()
                            provider.isEnabled
                        }
            }
            .field {
                it.name("source")
                        .description("Associated authentication source")
                        .type(authenticationSource.typeRef)
            }
            .build()

    override fun getTypeName(): String = AuthenticationSourceProvider::class.java.simpleName

}