package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.stringArgument
import net.nemerosa.ontrack.model.structure.ValidationStampService
import org.springframework.stereotype.Component

@Component
class GQLRootQueryValidationStampNames(
    private val validationStampService: ValidationStampService,
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
        .name("validationStampNames")
        .description("Gets a list of existing available validation stamp names")
        .argument(stringArgument(ARG_TOKEN, "Part of the name to look for", nullable = false))
        .type(listType(GraphQLString))
        .dataFetcher { env ->
            val token: String = env.getArgument(ARG_TOKEN)!!
            validationStampService.findValidationStampNames(token)
        }
        .build()

    companion object {
        const val ARG_TOKEN = "token"
    }
}