package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.support.booleanField
import net.nemerosa.ontrack.graphql.support.idFieldForID
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.model.annotations.getPropertyDescription
import net.nemerosa.ontrack.model.structure.PredefinedValidationStamp
import org.springframework.stereotype.Component

@Component
class GQLTypePredefinedValidationStamp(
    private val gqlTypeValidationDataTypeConfig: GQLTypeValidationDataTypeConfig,
) : GQLType {

    override fun getTypeName(): String = PredefinedValidationStamp::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
        .name(typeName)
        .description("Predefined validation stamp")
        .idFieldForID(PredefinedValidationStamp::id)
        .stringField(PredefinedValidationStamp::name)
        .stringField(PredefinedValidationStamp::description)
        .booleanField(PredefinedValidationStamp::isImage)
        .field {
            it.name(PredefinedValidationStamp::dataType.name)
                .description(getPropertyDescription(PredefinedValidationStamp::dataType))
                .type(gqlTypeValidationDataTypeConfig.typeRef)
        }
        .build()
}