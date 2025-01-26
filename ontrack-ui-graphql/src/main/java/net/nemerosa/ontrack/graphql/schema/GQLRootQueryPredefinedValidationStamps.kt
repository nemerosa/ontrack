package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.stringArgument
import net.nemerosa.ontrack.graphql.support.toTypeRef
import net.nemerosa.ontrack.model.settings.PredefinedValidationStampService
import net.nemerosa.ontrack.model.structure.PredefinedValidationStamp
import org.springframework.stereotype.Component

@Component
class GQLRootQueryPredefinedValidationStamps(
    private val predefinedValidationStampService: PredefinedValidationStampService,
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("predefinedValidationStamps")
            .description("Returns the list of all predefined validation stamps")
            .argument(stringArgument("name", "Filtering on the predefined validation stamp name"))
            .type(listType(PredefinedValidationStamp::class.toTypeRef()))
            .dataFetcher { env ->
                val name: String? = env.getArgument("name")
                if (name.isNullOrBlank()) {
                    predefinedValidationStampService.predefinedValidationStamps
                } else {
                    predefinedValidationStampService.findPredefinedValidationStamps(name)
                }
            }
            .build()

}