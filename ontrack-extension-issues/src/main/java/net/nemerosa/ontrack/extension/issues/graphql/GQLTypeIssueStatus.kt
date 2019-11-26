package net.nemerosa.ontrack.extension.issues.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.issues.model.IssueStatus
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class GQLTypeIssueStatus : GQLType {

    override fun getTypeName(): String = IssueStatus::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType {
        return GraphQLObjectType.newObject()
                .name(typeName)
                .description("Abstraction for the status of an issue")
                .stringField("name", "Name of the status")
                .build()
    }
}