package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import net.nemerosa.ontrack.model.json.schema.JsonSchemaDefinition
import org.springframework.stereotype.Component

@Component
class GQLTypeJsonSchemaDefinition : GQLType {

    override fun getTypeName(): String = JsonSchemaDefinition::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLBeanConverter.asObjectType(JsonSchemaDefinition::class, cache)
}