package net.nemerosa.ontrack.extension.issues.graphql

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

/**
 * Representation of an [Issue].
 */
@Component
class GQLTypeIssue(
        private val issueStatus: GQLTypeIssueStatus
) : GQLType {

    override fun getTypeName(): String = Issue::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType {
        return GraphQLObjectType.newObject()
                .name(typeName)
                .description("Representation of an Issue.")
                .stringField("key", "Key of the issue")
                .stringField("displayKey", "Display name for the issue")
                .stringField("summary", "Title / summary for the issue")
                .stringField("url", "URL to the issue")
                .field {
                    it.name("status")
                            .description("Status of the issue")
                            .type(issueStatus.typeRef)
                }
                .field {
                    it.name("updateTime")
                            .description("Last update time")
                            .type(GraphQLString)
                            .dataFetcher { env ->
                                val issue: Issue = env.getSource()
                                Time.store(issue.updateTime)
                            }
                }
                .build()
    }
}