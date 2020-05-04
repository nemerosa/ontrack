package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import net.nemerosa.ontrack.graphql.schema.security.GQLTypeAuthenticationSource
import net.nemerosa.ontrack.graphql.support.GraphqlUtils
import net.nemerosa.ontrack.model.security.AccountGroupMapping
import org.springframework.stereotype.Component

/**
 * @see net.nemerosa.ontrack.model.security.AccountGroupMapping
 */
@Component
class GQLTypeAccountGroupMapping(
        private val authenticationSource: GQLTypeAuthenticationSource
) : GQLType {

    override fun getTypeName(): String = ACCOUNT_GROUP_MAPPING

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
            .name(ACCOUNT_GROUP_MAPPING)
            .field(GraphqlUtils.idField())
            .field {
                it.name(AccountGroupMapping::authenticationSource.name)
                        .description("Associated authentication source")
                        .type(authenticationSource.typeRef)
            }
            .field(GraphqlUtils.stringField("name", "Name of the mapping"))
            .field {
                it.name("group")
                        .description("Associated group")
                        .type(GraphQLTypeReference(GQLTypeAccountGroup.ACCOUNT_GROUP))
            }
            .build()

    companion object {
        @JvmStatic
        val ACCOUNT_GROUP_MAPPING = AccountGroupMapping::class.java.simpleName
    }
}