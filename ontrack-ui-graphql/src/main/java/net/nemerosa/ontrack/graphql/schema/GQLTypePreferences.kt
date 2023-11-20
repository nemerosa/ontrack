package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import net.nemerosa.ontrack.model.annotations.getAPITypeName
import net.nemerosa.ontrack.model.preferences.Preferences
import org.springframework.stereotype.Component

@Component
class GQLTypePreferences : GQLType {

    override fun getTypeName(): String = Preferences::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description(getAPITypeName(Preferences::class))
            .fields(GraphQLBeanConverter.asObjectFields(Preferences::class, cache))
            .build()

}