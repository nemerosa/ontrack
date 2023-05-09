package net.nemerosa.ontrack.kdsl.acceptance.tests.queue

import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import net.nemerosa.ontrack.kdsl.spec.Ontrack
import net.nemerosa.ontrack.kdsl.spec.extension.queue.QueueRecordState
import net.nemerosa.ontrack.kdsl.spec.extension.queue.queue

class QueueACCTestSupport(
        private val ontrack: Ontrack,
) {

    fun waitForQueueRecordToBe(queueID: String, state: QueueRecordState) {
        waitUntil(
                task = "Queue record $queueID must be in state $state",
                initial = 1_000L,
                timeout = 30_000L,
        ) {
            val record = ontrack.queue.findQueueRecordByID(queueID)
            record != null && record.state == state
        }
    }

    fun postMockMessage(message: String): String =
            ontrack.queue.mock.post(message)
}
