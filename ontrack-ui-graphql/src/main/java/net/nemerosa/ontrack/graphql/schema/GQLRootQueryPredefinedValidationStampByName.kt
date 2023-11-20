package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.support.stringArgument
import net.nemerosa.ontrack.model.settings.PredefinedValidationStampService
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class GQLRootQueryPredefinedValidationStampByName(
    private val gqlTypePredefinedValidationStamp: GQLTypePredefinedValidationStamp,
    private val predefinedValidationStampService: PredefinedValidationStampService,
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("predefinedValidationStampByName")
            .description("Gets a predefined validation stamps by name")
            .type(gqlTypePredefinedValidationStamp.typeRef)
            .argument(stringArgument("name", "Name", nullable = false))
            .dataFetcher { env ->
                val name: String = env.getArgument("name")
                predefinedValidationStampService.findPredefinedValidationStampByName(name)
                    .getOrNull()
            }
            .build()
}