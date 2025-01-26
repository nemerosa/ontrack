package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.support.GQLScalarJSON
import net.nemerosa.ontrack.model.structure.ValidationDataTypeConfig
import org.springframework.stereotype.Component

/**
 * GraphQL type for [ValidationDataTypeConfig].
 */
@Component
class GQLTypeValidationDataTypeConfig(
    private val validationDataTypeDescriptor: GQLTypeValidationDataTypeDescriptor
) : GQLType {

    override fun getTypeName() = ValidationDataTypeConfig::class.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Configuration for the data type associated with a validation stamp")
            .field {
                it.name("descriptor")
                    .description("Descriptor for the validation data type")
                    .type(validationDataTypeDescriptor.typeRef)
            }
            .field {
                it.name("config")
                    .description("Configuration object")
                    .type(GQLScalarJSON.INSTANCE)
            }
            .build()
}