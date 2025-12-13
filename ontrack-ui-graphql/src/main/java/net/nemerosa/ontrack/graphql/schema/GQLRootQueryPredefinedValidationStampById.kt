package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.support.intArgument
import net.nemerosa.ontrack.graphql.support.toTypeRef
import net.nemerosa.ontrack.model.settings.PredefinedValidationStampService
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.PredefinedValidationStamp
import org.springframework.stereotype.Component

@Component
class GQLRootQueryPredefinedValidationStampById(
    private val predefinedValidationStampService: PredefinedValidationStampService,
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("predefinedValidationStampById")
            .description("Predefined validation stamp by ID")
            .type(PredefinedValidationStamp::class.toTypeRef())
            .argument(intArgument("id", "ID", nullable = false))
            .dataFetcher { env ->
                val id: Int = env.getArgument("id")!!
                predefinedValidationStampService.getPredefinedValidationStamp(ID.of(id))
            }
            .build()
}