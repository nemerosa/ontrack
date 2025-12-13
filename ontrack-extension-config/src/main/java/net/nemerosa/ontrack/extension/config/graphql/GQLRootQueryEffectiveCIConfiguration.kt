package net.nemerosa.ontrack.extension.config.graphql

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.extension.config.ci.CIConfigurationService
import net.nemerosa.ontrack.extension.config.model.ConfigureBuildInput
import net.nemerosa.ontrack.graphql.schema.GQLRootQuery
import net.nemerosa.ontrack.graphql.support.GQLScalarJSON
import net.nemerosa.ontrack.graphql.support.typedArgument
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import org.springframework.stereotype.Component

/**
 * Getting the effective configuration for the current CI without actually configuring anything
 */
@Component
class GQLRootQueryEffectiveCIConfiguration(
    private val ciConfigurationService: CIConfigurationService,
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("effectiveCIConfiguration")
            .description("Getting the effective configuration for the current CI without actually configuring anything")
            .argument(
                typedArgument(
                    name = "input",
                    typeName = ConfigureBuildInput::class.java.simpleName,
                    description = "Input for the CI configuration",
                    nullable = false,
                )
            )
            .type(GQLScalarJSON.INSTANCE)
            .dataFetcher { env ->
                val input = env.getArgument<Any>("input").asJson().parse<ConfigureBuildInput>()
                ciConfigurationService.effectiveCIConfiguration(
                    config = input.config,
                    ci = input.ci,
                    scm = input.scm,
                    env = input.env,
                )
            }
            .build()

}