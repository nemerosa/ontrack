package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.model.security.Account
import net.nemerosa.ontrack.model.security.AccountGroup
import org.springframework.stereotype.Component

@Component
class GQLTypeProjectAuthorization : GQLType {

    override fun getTypeName(): String = PROJECT_AUTHORIZATION

    override fun createType(cache: GQLTypeCache): GraphQLObjectType {
        return GraphQLObjectType.newObject()
                .name(PROJECT_AUTHORIZATION)
                .stringField("id", "ID of the role")
                .stringField("name", "Unique name for the role")
                .stringField("description", "Description of the role")
                // List of groups
                .field {
                    it.name("groups")
                            .description("List of groups having this role")
                            .type(listType(GQLTypeAccountGroup.ACCOUNT_GROUP))
                }
                // List of accounts
                .field {
                    it.name("accounts")
                            .description("List of accounts having this role")
                            .type(listType(GQLTypeAccount.ACCOUNT))
                }
                // OK
                .build()
    }

    data class Model(
            val id: String,
            val name: String,
            val description: String,
            val groups: Collection<AccountGroup>,
            val accounts: Collection<Account>,
    )

    companion object {
        const val PROJECT_AUTHORIZATION = "ProjectAuthorization"
    }
}
