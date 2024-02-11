package net.nemerosa.ontrack.extension.scm.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.issues.graphql.GQLTypeIssue
import net.nemerosa.ontrack.extension.issues.graphql.IssueServiceConfigurationRepresentationGQLType
import net.nemerosa.ontrack.extension.scm.changelog.SCMChangeLogIssues
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.getTypeDescription
import net.nemerosa.ontrack.graphql.support.listType
import net.nemerosa.ontrack.model.annotations.getPropertyDescription
import org.springframework.stereotype.Component

@Component
class GQLTypeSCMChangeLogIssues(
    private val gqlTypeIssue: GQLTypeIssue,
    private val issueServiceConfigurationRepresentationGQLType: IssueServiceConfigurationRepresentationGQLType,
) : GQLType {

    override fun getTypeName(): String = SCMChangeLogIssues::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description(getTypeDescription(SCMChangeLogIssues::class))
            .field {
                it.name(SCMChangeLogIssues::issues.name)
                    .description(getPropertyDescription(SCMChangeLogIssues::issues))
                    .type(listType(gqlTypeIssue.typeRef))
            }
            .field(
                SCMChangeLogIssues::issueServiceConfiguration,
                issueServiceConfigurationRepresentationGQLType
            )
            .build()
}