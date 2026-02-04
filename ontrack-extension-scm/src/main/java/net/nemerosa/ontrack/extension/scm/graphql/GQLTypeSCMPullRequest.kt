package net.nemerosa.ontrack.extension.scm.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.scm.service.SCMPullRequest
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.enumField
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GQLTypeSCMPullRequest : GQLType {

    override fun getTypeName(): String = SCMPullRequest::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Information about a pull request")
            .stringField(SCMPullRequest::id)
            .stringField(SCMPullRequest::name)
            .stringField(SCMPullRequest::link)
            .enumField(SCMPullRequest::status)
            .build()
}