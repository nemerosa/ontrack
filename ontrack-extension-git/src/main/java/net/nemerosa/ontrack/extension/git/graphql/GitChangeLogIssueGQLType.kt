package net.nemerosa.ontrack.extension.git.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.git.model.GitChangeLogIssue
import net.nemerosa.ontrack.extension.issues.graphql.GQLTypeIssue
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.GQLScalarJSON
import net.nemerosa.ontrack.graphql.support.gqlTypeField
import net.nemerosa.ontrack.json.asJson
import org.springframework.stereotype.Component

/**
 * GraphQL type for [GitChangeLogIssue]
 */
@Component
class GitChangeLogIssueGQLType(
    private val issueGQLType: GQLTypeIssue,
) : GQLType {

    override fun getTypeName(): String = GitChangeLogIssue::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
        .name(typeName)
        .description("Description for an issue")
        // Issue object
        .field {
            it.name("issueObject")
                .description("Issue representation as JSON")
                .type(GQLScalarJSON.INSTANCE)
                .dataFetcher { env ->
                    val gitChangeLogIssue: GitChangeLogIssue = env.getSource()
                    gitChangeLogIssue.issue.asJson()
                }
        }
        // Issue representation
        .gqlTypeField("issue", "Issue representation with minimal fields", issueGQLType, nullable = false)
        // OK
        .build()
}