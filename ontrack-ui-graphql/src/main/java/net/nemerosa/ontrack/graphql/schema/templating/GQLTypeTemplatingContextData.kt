package net.nemerosa.ontrack.graphql.schema.templating

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.jsonField
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.model.templating.TemplatingContextData
import org.springframework.stereotype.Component

@Component
class GQLTypeTemplatingContextData : GQLType {

    override fun getTypeName(): String = TemplatingContextData::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Data to be rendered in a template")
            .stringField(TemplatingContextData::id)
            .jsonField(TemplatingContextData::data)
            .build()
}