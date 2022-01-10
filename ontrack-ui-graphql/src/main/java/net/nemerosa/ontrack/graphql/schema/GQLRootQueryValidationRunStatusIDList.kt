package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.model.structure.ValidationRunStatusService
import org.springframework.stereotype.Component

@Component
class GQLRootQueryValidationRunStatusIDList(
    private val validationRunStatusService: ValidationRunStatusService,
    private val gqlTypeValidationRunStatusID: GQLTypeValidationRunStatusID,
) : GQLRootQuery {

    override fun getFieldDefinition(): GraphQLFieldDefinition = GraphQLFieldDefinition.newFieldDefinition()
        .name("validationRunStatusIDList")
        .description("List of validation run statuses")
        .type(listType(gqlTypeValidationRunStatusID.typeRef))
        .dataFetcher { _ ->
            validationRunStatusService.validationRunStatusList
        }
        .build()

}