package net.nemerosa.ontrack.extension.git.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.git.model.OntrackGitIssueInfo
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.GQLScalarJSON
import net.nemerosa.ontrack.json.toJson
import org.springframework.stereotype.Component

/**
 * GraphQL type for [OntrackGitIssueInfo].
 */
@Component
class OntrackGitIssueInfoGQLType(
        private val ontrackGitCommitInfoGQLType: OntrackGitCommitInfoGQLType,
        private val issueServiceConfigurationRepresentationGQLType: IssueServiceConfigurationRepresentationGQLType
) : GQLType {
    override fun getTypeName(): String =
            OntrackGitIssueInfo::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
            GraphQLObjectType.newObject()
                    .name(typeName)
                    .description("Information about an issue")
                    // Issue service configuration representation
                    .field {
                        it.name("issueServiceConfigurationRepresentation")
                                .description("Issue service associated with the issue")
                                .type(issueServiceConfigurationRepresentationGQLType.typeRef)
                    }
                    // Issue as JSON
                    .field {
                        it.name("issue")
                                .description("JSON representation of the issue")
                                .type(GQLScalarJSON.INSTANCE)
                                .dataFetcher { environment ->
                                    val gitIssueInfo = environment.getSource<OntrackGitIssueInfo>()
                                    gitIssueInfo.issue.toJson()
                                }
                    }
                    // Commit info
                    .field {
                        it.name("commitInfo")
                                .description("Information about the last commit associated with this issue")
                                .type(ontrackGitCommitInfoGQLType.typeRef)
                    }
                    // OK
                    .build()
}