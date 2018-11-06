package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.support.GQLScalarJSON
import org.springframework.stereotype.Component

/**
 * GraphQL type for [net.nemerosa.ontrack.model.structure.ValidationRunData].
 */
@Component
class GQLTypeValidationRunData(
        private val validationDataTypeDescriptor: GQLTypeValidationDataTypeDescriptor
) : GQLType {

    override fun getTypeName() = "ValidationRunData"

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
            GraphQLObjectType.newObject()
                    .name(typeName)
                    .description("Data associated with a validation run")
                    .field {
                        it.name("descriptor")
                                .description("Descriptor for the validation data type")
                                .type(validationDataTypeDescriptor.typeRef)
                    }
                    .field {
                        it.name("data")
                                .description("Data object")
                                .type(GQLScalarJSON.INSTANCE)
                    }
                    .build()
}