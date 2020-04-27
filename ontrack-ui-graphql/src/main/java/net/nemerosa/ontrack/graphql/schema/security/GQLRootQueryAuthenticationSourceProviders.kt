package net.nemerosa.ontrack.graphql.schema.security

import graphql.Scalars.GraphQLBoolean
import graphql.schema.DataFetcher
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.GraphqlUtils.stdList
import net.nemerosa.ontrack.model.security.AuthenticationSourceService
import org.springframework.stereotype.Component

/**
 * List of all authentication sources.
 */
@Component
class GQLRootQueryAuthenticationSourceProviders(
        private val authenticationSource: GQLTypeAuthenticationSource,
        private val authenticationSourceService: AuthenticationSourceService
) : GQLRootQuery {

    companion object {
        const val ARG_ENABLED = "enabled"
        const val ARG_GROUP_MAPPING_SUPPORTED = "groupMappingSupported"
        const val ARG_ALLOWING_PASSWORD_CHANGE = "allowingPasswordChange"
    }

    override fun getFieldDefinition(): GraphQLFieldDefinition =
            GraphQLFieldDefinition.newFieldDefinition()
                    .name("authenticationSourceProviders")
                    .description("List of all authentication source providers.")
                    .argument {
                        it.name(ARG_ENABLED)
                                .description("Filters on authentication source providers which are enabled")
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
                    .type(stdList(authenticationSource.typeRef))
                    .dataFetcher(authenticationSourcesDataFetcher())
                    .build()

    private fun authenticationSourcesDataFetcher() = DataFetcher { env ->
        val enabled: Boolean? = env.getArgument(ARG_ENABLED)
        val groupMappingSupported: Boolean? = env.getArgument(ARG_GROUP_MAPPING_SUPPORTED)
        val allowingPasswordChange: Boolean? = env.getArgument(ARG_ALLOWING_PASSWORD_CHANGE)
        authenticationSourceService.authenticationSourceProviders
                .filter { enabled == null || enabled == it.isEnabled }
                .filter { groupMappingSupported == null || groupMappingSupported == it.source.isGroupMappingSupported }
                .filter { allowingPasswordChange == null || allowingPasswordChange == it.source.isAllowingPasswordChange }
    }

}