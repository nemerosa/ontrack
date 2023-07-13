package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.support.booleanField
import net.nemerosa.ontrack.graphql.support.idField
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.model.structure.PredefinedValidationStamp
import org.springframework.stereotype.Component

@Component
class GQLTypePredefinedValidationStamp : GQLType {

    override fun getTypeName(): String = PredefinedValidationStamp::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
        .name(typeName)
        .description("Predefined validation stamp")
        .idField(PredefinedValidationStamp::id)
        .stringField(PredefinedValidationStamp::name)
        .stringField(PredefinedValidationStamp::description)
        .booleanField(PredefinedValidationStamp::isImage)
        .build()
}