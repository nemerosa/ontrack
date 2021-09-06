package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLInputType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter.asInputType
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter.asObject
import net.nemerosa.ontrack.model.structure.BuildSearchForm
import org.springframework.stereotype.Component

@Component
class GQLInputBuildSearchForm : GQLInputType<BuildSearchForm> {

    override fun getTypeRef(): GraphQLTypeReference = GraphQLTypeReference(BuildSearchForm::class.java.simpleName)

    override fun createInputType(): GraphQLInputType = asInputType(BuildSearchForm::class)

    override fun convert(argument: Any?): BuildSearchForm =
        asObject(argument, BuildSearchForm::class.java) ?: BuildSearchForm().withMaximumCount(10)
}