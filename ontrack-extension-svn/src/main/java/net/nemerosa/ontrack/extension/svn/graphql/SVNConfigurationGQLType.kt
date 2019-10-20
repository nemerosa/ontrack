package net.nemerosa.ontrack.extension.svn.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.svn.model.SVNConfiguration
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import org.springframework.stereotype.Component

@Component
class SVNConfigurationGQLType : GQLType {

    override fun getTypeName(): String = SVNConfiguration::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType {
        return GraphQLBeanConverter.asObjectType(SVNConfiguration::class.java, cache, setOf("password", "descriptor", "credentials"))
    }
}
