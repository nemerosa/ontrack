package net.nemerosa.ontrack.graphql.schema.configurations

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.stringArgument
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.support.Configuration
import net.nemerosa.ontrack.model.support.ConfigurationService
import net.nemerosa.ontrack.model.support.ConfigurationServiceFactory
import org.springframework.stereotype.Component

@Component
class GQLRootQueryConfigurations(
    private val gqlTypeConfiguration: GQLTypeConfiguration,
    private val configurationServiceFactory: ConfigurationServiceFactory,
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("configurations")
            .description("Gets a list of configuration for a given type.")
            .argument(stringArgument(ARG_CONFIGURATION_TYPE, "Configuration type", nullable = false))
            .type(listType(gqlTypeConfiguration.typeRef))
            .dataFetcher { env ->
                val configurationType: String = env.getArgument(ARG_CONFIGURATION_TYPE)!!
                val configurationService = configurationServiceFactory.findConfigurationService(configurationType)
                configurationService?.let {
                    getConfigurationDataList(configurationService)
                } ?: emptyList<GQLTypeConfiguration.Data>()
            }
            .build()

    private fun <T : Configuration<T>> getConfigurationDataList(configurationService: ConfigurationService<T>): List<GQLTypeConfiguration.Data> =
        configurationService.configurations
            .map {
                GQLTypeConfiguration.Data(
                    name = it.name,
                    data = it.obfuscate().asJson(),
                    extra = configurationService.getConfigExtraData(it).asJson(),
                )
            }

    companion object {
        const val ARG_CONFIGURATION_TYPE = "configurationType"
    }

}