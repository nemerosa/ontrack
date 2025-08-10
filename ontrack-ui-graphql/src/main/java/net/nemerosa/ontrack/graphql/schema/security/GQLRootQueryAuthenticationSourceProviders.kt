package net.nemerosa.ontrack.graphql.schema.security

import graphql.Scalars.GraphQLBoolean
import graphql.schema.DataFetcher
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.model.security.AuthenticationSourceRepository
import org.springframework.stereotype.Component

/**
 * List of all authentication sources.
 */
@Component
class GQLRootQueryAuthenticationSourceProviders(
        private val authenticationSource: GQLTypeAuthenticationSource,
        private val authenticationSourceRepository: AuthenticationSourceRepository
) : GQLRootQuery {

    companion object {
        const val ARG_ENABLED = "enabled"
        const val ARG_GROUP_MAPPING_SUPPORTED = "groupMappingSupported"
        const val ARG_ALLOWING_PASSWORD_CHANGE = "allowingPasswordChange"
    }

    override fun getFieldDefinition(): GraphQLFieldDefinition =
            GraphQLFieldDefinition.newFieldDefinition()
                    .name("authenticationSources")
                    .description("List of all authentication sources.")
                    .deprecate("Will be removed in V5. No replacement.")
                    .argument {
                        it.name(ARG_ENABLED)
                                .description("Filters on authentication source which are enabled")
                                .type(GraphQLBoolean)
                    }
                    .argument {
                        it.name(ARG_GROUP_MAPPING_SUPPORTED)
                                .description("Filters on authentication sources which support group mapping")
                                .type(GraphQLBoolean)
                    }
                    .argument {
                        it.name(ARG_ALLOWING_PASSWORD_CHANGE)
                                .description("Filters on authentication sources which allow the user's password to be changed")
                                .type(GraphQLBoolean)
                    }
                    .type(listType(authenticationSource.typeRef))
                    .dataFetcher(authenticationSourcesDataFetcher())
                    .build()

    private fun authenticationSourcesDataFetcher() = DataFetcher { env ->
        val enabled: Boolean? = env.getArgument(ARG_ENABLED)
        val groupMappingSupported: Boolean? = env.getArgument(ARG_GROUP_MAPPING_SUPPORTED)
        val allowingPasswordChange: Boolean? = env.getArgument(ARG_ALLOWING_PASSWORD_CHANGE)
        authenticationSourceRepository.authenticationSources
                .filter { enabled == null || enabled == it.isEnabled }
                .filter { groupMappingSupported == null || groupMappingSupported == it.isGroupMappingSupported }
                .filter { allowingPasswordChange == null || allowingPasswordChange == it.isAllowingPasswordChange }
    }

}