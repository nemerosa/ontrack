package net.nemerosa.ontrack.extension.av.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfig
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import org.springframework.stereotype.Component

@Component
class GQLTypeAutoVersioningConfig : GQLType {

    override fun getTypeName(): String = AutoVersioningConfig::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLBeanConverter.asObjectType(AutoVersioningConfig::class, cache)
}