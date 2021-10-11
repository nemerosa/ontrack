package net.nemerosa.ontrack.extension.github.ui.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.github.client.GitHubRateLimit
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import org.springframework.stereotype.Component

@Component
class GQLTypeGitHubRateLimit(
    private val gqlTypeRateLimit: GQLTypeRateLimit,
) : GQLType {

    override fun getTypeName(): String = GitHubRateLimit::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
        .name(typeName)
        .description("Rate limits")
        .field {
            it.name(GitHubRateLimit::core.name)
                .description("Rate limit for core calls")
                .type(gqlTypeRateLimit.typeRef)
        }
        .field {
            it.name(GitHubRateLimit::graphql.name)
                .description("Rate limit for GraphQL calls")
                .type(gqlTypeRateLimit.typeRef)
        }
        .build()
}