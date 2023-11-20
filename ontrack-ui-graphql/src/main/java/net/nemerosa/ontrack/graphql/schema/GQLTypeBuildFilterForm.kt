package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.support.booleanField
import net.nemerosa.ontrack.graphql.support.classField
import net.nemerosa.ontrack.graphql.support.jsonField
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.model.buildfilter.BuildFilterForm
import org.springframework.stereotype.Component

/**
 * GraphQL type for [BuildFilterForm].
 */
@Component
class GQLTypeBuildFilterForm : GQLType {
    override fun getTypeName(): String = BuildFilterForm::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
            .name(typeName)
            .description("Form to create a new build filter.")
            .classField(BuildFilterForm::type)
            .stringField(BuildFilterForm::typeName)
            .booleanField(BuildFilterForm::isPredefined)
            .jsonField(BuildFilterForm::form, deprecation = "Form is only used for the Ontrack legacy UI")
            .build()
}