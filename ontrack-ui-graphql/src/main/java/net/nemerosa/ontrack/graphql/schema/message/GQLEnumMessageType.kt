package net.nemerosa.ontrack.graphql.schema.message

import net.nemerosa.ontrack.graphql.schema.AbstractGQLEnum
import net.nemerosa.ontrack.model.message.MessageType
import org.springframework.stereotype.Component

@Component
class GQLEnumMessageType : AbstractGQLEnum<MessageType>(
    type = MessageType::class,
    values = MessageType.values(),
    description = "List of types of messages"
)