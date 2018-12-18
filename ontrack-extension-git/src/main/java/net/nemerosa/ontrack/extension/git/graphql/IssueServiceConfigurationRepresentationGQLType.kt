package net.nemerosa.ontrack.extension.git.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.stringField
import org.springframework.stereotype.Component

@Component
class IssueServiceConfigurationRepresentationGQLType : GQLType {

    override fun getTypeName(): String = IssueServiceConfigurationRepresentation::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType {
        return GraphQLObjectType.newObject()
                .stringField("id", "ID of the issue service")
                .stringField("name", "Name of the issue service")
                .stringField("serviceId", "Link to the issue service configuration")
                .build()
    }
}