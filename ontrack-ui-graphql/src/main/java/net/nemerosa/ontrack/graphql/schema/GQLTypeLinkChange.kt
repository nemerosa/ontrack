package net.nemerosa.ontrack.graphql.schema

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.support.field
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.model.structure.LinkChange
import org.springframework.stereotype.Component

@Component
class GQLTypeLinkChange : GQLType {

    override fun getTypeName(): String = LinkChange::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType = GraphQLObjectType.newObject()
        .name(typeName)
        .description("Description of a change on a linked dependency.")
        // Main fields
        .field(LinkChange::project)
        .stringField(LinkChange::qualifier)
        .field(LinkChange::from)
        .field(LinkChange::to)
        // OK
        .build()
}
