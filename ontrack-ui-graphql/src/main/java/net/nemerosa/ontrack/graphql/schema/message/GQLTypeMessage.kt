package net.nemerosa.ontrack.graphql.schema.message

import graphql.schema.GraphQLObjectType
import net.nemerosa.ontrack.graphql.schema.GQLType
import net.nemerosa.ontrack.graphql.schema.GQLTypeCache
import net.nemerosa.ontrack.graphql.support.enumField
import net.nemerosa.ontrack.graphql.support.stringField
import net.nemerosa.ontrack.model.message.Message
import org.springframework.stereotype.Component

@Component
class GQLTypeMessage : GQLType {

    override fun getTypeName(): String = Message::class.java.simpleName

    override fun createType(cache: GQLTypeCache): GraphQLObjectType =
        GraphQLObjectType.newObject()
            .name(typeName)
            .description("Message")
            .stringField(Message::content)
            .enumField(Message::type)
            .build()
}