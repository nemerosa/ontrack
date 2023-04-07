package net.nemerosa.ontrack.extension.queue.ui

import net.nemerosa.ontrack.extension.queue.record.QueueRecordCleanupService
import net.nemerosa.ontrack.graphql.schema.Mutation
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import org.springframework.stereotype.Component

@Component
class QueueRecordMutations(
        private val queueRecordCleanupService: QueueRecordCleanupService,
) : TypedMutationProvider() {
    override val mutations: List<Mutation> = listOf(
            unitNoInputMutation(
                    name = "purgeQueueRecords",
                    description = "Deletes all queue records"
            ) {
                queueRecordCleanupService.purge()
            }
    )
}