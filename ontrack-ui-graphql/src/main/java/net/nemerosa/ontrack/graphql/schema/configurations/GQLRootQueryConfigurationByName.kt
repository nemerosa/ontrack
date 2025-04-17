package net.nemerosa.ontrack.graphql.schema.configurations

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.stringArgument
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.support.Configuration
import net.nemerosa.ontrack.model.support.ConfigurationService
import net.nemerosa.ontrack.model.support.ConfigurationServiceFactory
import org.springframework.stereotype.Component

@Component
class GQLRootQueryConfigurationByName(
    private val gqlTypeConfiguration: GQLTypeConfiguration,
    private val configurationServiceFactory: ConfigurationServiceFactory,
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("configurationByName")
            .description("Gets a configuration for a given type and name")
            .argument(stringArgument(ARG_CONFIGURATION_TYPE, "Configuration type", nullable = false))
            .argument(stringArgument(ARG_NAME, "Configuration name", nullable = false))
            .type(gqlTypeConfiguration.typeRef)
            .dataFetcher { env ->
                val configurationType: String = env.getArgument(ARG_CONFIGURATION_TYPE)!!
                val configurationName: String = env.getArgument(ARG_NAME)!!
                val configurationService = configurationServiceFactory.findConfigurationService(configurationType)
                configurationService?.let {
                    getConfiguration(it, configurationName)
                }
            }
            .build()

    private fun <T : Configuration<T>> getConfiguration(
        configurationService: ConfigurationService<T>,
        configurationName: String,
    ): GQLTypeConfiguration.Data? =
        configurationService.findConfiguration(configurationName)
            ?.let {
                GQLTypeConfiguration.Data(
                    name = it.name,
                    data = it.obfuscate().asJson(),
                    extra = configurationService.getConfigExtraData(it).asJson(),
                )
            }

    companion object {
        const val ARG_CONFIGURATION_TYPE = "type"
        const val ARG_NAME = "name"
    }

}