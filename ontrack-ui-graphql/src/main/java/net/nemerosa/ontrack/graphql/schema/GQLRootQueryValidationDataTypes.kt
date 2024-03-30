package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLFieldDefinition
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.model.structure.ValidationDataTypeService
import org.springframework.stereotype.Component

@Component
class GQLRootQueryValidationDataTypes(
    private val gqlTypeValidationDataTypeDescriptor: GQLTypeValidationDataTypeDescriptor,
    private val validationDataTypeService: ValidationDataTypeService,
) : GQLRootQuery {
    override fun getFieldDefinition(): GraphQLFieldDefinition =
        GraphQLFieldDefinition.newFieldDefinition()
            .name("validationDataTypes")
            .description("List of all available validation data types")
            .type(listType(gqlTypeValidationDataTypeDescriptor.typeRef))
            .dataFetcher {
                validationDataTypeService.getAllTypes().map { type ->
                    type.descriptor
                }
            }
            .build()
}