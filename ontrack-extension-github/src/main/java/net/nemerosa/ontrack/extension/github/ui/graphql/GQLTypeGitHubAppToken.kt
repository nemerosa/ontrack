package net.nemerosa.ontrack.extension.github.ui.graphql

import graphql.Scalars.GraphQLBoolean
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.github.app.GitHubAppToken
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.dateField
import org.springframework.stereotype.Component

@Component
class GQLTypeGitHubAppToken : GQLType {

    override fun getTypeName(): String = GitHubAppToken::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
        .name(typeName)
        .description("GitHub App token information")
        .field {
            it.name("valid")
                .description("Is the token valid?")
                .type(GraphQLBoolean)
                .dataFetcher { env ->
                    val token: GitHubAppToken = env.getSource()
                    token.isValid()
                }
        }
        .dateField(GitHubAppToken::validUntil.name, "Date of validity")
        .build()
}