package net.nemerosa.ontrack.graphql.schema.security

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.GraphQLBeanConverter
import net.nemerosa.ontrack.model.security.AuthenticationSource
import org.springframework.stereotype.Component

@Component
class GQLTypeAuthenticationSource : GQLType {

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLBeanConverter.asObjectType(AuthenticationSource::class, cache)

    override fun getTypeName(): String = AuthenticationSource::class.java.simpleName

}