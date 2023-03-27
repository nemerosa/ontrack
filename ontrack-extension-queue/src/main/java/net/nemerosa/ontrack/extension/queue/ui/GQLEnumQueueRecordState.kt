package net.nemerosa.ontrack.extension.queue.ui

import net.nemerosa.ontrack.extension.queue.record.QueueRecordState
import net.nemerosa.ontrack.graphql.schema.AbstractGQLEnum
import org.springframework.stereotype.Component

@Component
class GQLEnumQueueRecordState : AbstractGQLEnum<QueueRecordState>(
    QueueRecordState::class,
    QueueRecordState.values(),
    "State of a message in a queue"
)