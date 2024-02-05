package net.nemerosa.ontrack.extension.scm.graphql

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.extension.scm.changelog.SCMChangeLogCommit
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.dateField
import net.nemerosa.ontrack.graphql.support.getTypeDescription
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.model.annotations.getPropertyDescription
import org.springframework.stereotype.Component

@Component
class GQLTypeSCMChangeLogCommit : GQLType {

    override fun getTypeName(): String = SCMChangeLogCommit::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description(getTypeDescription(SCMChangeLogCommit::class))
            .stringField(SCMChangeLogCommit::id)
            .stringField(SCMChangeLogCommit::shortId)
            .stringField(SCMChangeLogCommit::author)
            .stringField(SCMChangeLogCommit::authorEmail)
            .stringField(SCMChangeLogCommit::message)
            .stringField(SCMChangeLogCommit::formattedMessage)
            .stringField(SCMChangeLogCommit::link)
            .dateField(
                name = SCMChangeLogCommit::timestamp.name,
                description = getPropertyDescription(SCMChangeLogCommit::timestamp),
                nullable = false
            )
            .build()
}
