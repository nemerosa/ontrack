package net.nemerosa.ontrack.extension.queue.ui

import net.nemerosa.ontrack.extension.queue.dispatching.QueueDispatchResultType
import net.nemerosa.ontrack.graphql.schema.AbstractGQLEnum
import org.springframework.stereotype.Component

@Component
class GQLEnumQueueDispatchResultType : AbstractGQLEnum<QueueDispatchResultType>(
        QueueDispatchResultType::class,
        QueueDispatchResultType.values(),
        "Result of a dispaching on a queue"
)