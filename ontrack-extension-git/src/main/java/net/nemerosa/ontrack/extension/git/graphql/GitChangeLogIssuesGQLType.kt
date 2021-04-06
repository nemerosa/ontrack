package net.nemerosa.ontrack.extension.git.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.git.model.GitChangeLogIssues
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.listType
import org.springframework.stereotype.Component

@Component
class GitChangeLogIssuesGQLType(
    private val issueServiceConfigurationRepresentationGQLType: IssueServiceConfigurationRepresentationGQLType,
    private val gitChangeLogIssueGQLType: GitChangeLogIssueGQLType
) : GQLType {

    override fun getTypeName(): String = GitChangeLogIssues::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
        .name(typeName)
        .description("List of issues associated with their issue service representation")
        // Issue service configuration
        .field {
            it.name(GitChangeLogIssues::issueServiceConfiguration.name)
                .description("Configuration of the issue service associated with this list of issues")
                .type(issueServiceConfigurationRepresentationGQLType.typeRef)
        }
        // List of issues
        .field {
            it.name(GitChangeLogIssues::list.name)
                .description("List of issues")
                .type(listType(gitChangeLogIssueGQLType.typeRef))
        }
        // OK
        .build()
}