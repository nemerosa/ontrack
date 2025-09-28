package net.nemerosa.ontrack.extension.scm.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.scm.SCMIssueInfo
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.field
import org.springframework.stereotype.Component

@Component
class GQLTypeSCMIssueInfo : GQLType {
    override fun getTypeName(): String = SCMIssueInfo::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Information about an issue and linked SCM information.")
            .field(SCMIssueInfo::issueServiceConfigurationRepresentation)
            .field(SCMIssueInfo::issue)
            .field(SCMIssueInfo::scmCommitInfo)
            .build()
}