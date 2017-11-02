package net.nemerosa.ontrack.graphql.schema

import graphql.Scalars
import graphql.schema.GraphQLObjectType
import org.springframework.stereotype.Component

/**
 * GraphQL type for [net.nemerosa.ontrack.model.structure.ValidationDataTypeDescriptor].
 */
@Component
class GQLTypeValidationDataTypeDescriptor(
        private val extensionFeatureDescription: GQLTypeExtensionFeatureDescription
) : GQLType {

    override fun getTypeName() = "ValidationDataTypeDescriptor"

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
            GraphQLObjectType.newObject()
                    .name(typeName)
                    .description("Descriptor for a validation data type")
                    .field {
                        it.name("extension")
                                .description("Associated extension feature")
                                .type(extensionFeatureDescription.typeRef)
                    }
                    .field {
                        it.name("id")
                                .description("ID (FQDN) of the validation data type")
                                .type(Scalars.GraphQLString)
                    }
                    .field {
                        it.name("displayName")
                                .description("Display name of the validation data type")
                                .type(Scalars.GraphQLString)
                    }
                    .build()
}