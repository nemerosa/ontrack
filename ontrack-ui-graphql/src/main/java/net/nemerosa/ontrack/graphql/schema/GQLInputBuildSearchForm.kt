package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLInputType
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.annotations.getAPITypeName
import net.nemerosa.ontrack.model.structure.BuildSearchForm
import org.springframework.stereotype.Component

@Component
class GQLInputBuildSearchForm : GQLInputType<BuildSearchForm> {

    private val typeName: String = BuildSearchForm::class.java.simpleName

    override fun getTypeRef(): GraphQLTypeReference = GraphQLTypeReference(typeName)

    override fun createInputType(dictionary: MutableSet<GraphQLType>): GraphQLInputType = GraphQLInputObjectType.newInputObject()
        .name(typeName)
        .description(getAPITypeName(BuildSearchForm::class))
        .field(intInputField(BuildSearchForm::maximumCount, nullable = true))
        .field(stringInputField(BuildSearchForm::branchName))
        .field(stringInputField(BuildSearchForm::buildName))
        .field(stringInputField(BuildSearchForm::promotionName))
        .field(stringInputField(BuildSearchForm::validationStampName))
        .field(stringInputField(BuildSearchForm::property))
        .field(stringInputField(BuildSearchForm::propertyValue))
        .field(booleanInputField(BuildSearchForm::buildExactMatch, nullable = true))
        .field(stringInputField(BuildSearchForm::linkedFrom))
        .field(stringInputField(BuildSearchForm::linkedTo))
        .build()

    override fun convert(argument: Any?): BuildSearchForm = argument?.asJson()?.parse() ?: BuildSearchForm()
}