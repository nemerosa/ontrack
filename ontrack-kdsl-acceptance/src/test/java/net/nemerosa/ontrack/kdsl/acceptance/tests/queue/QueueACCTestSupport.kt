package net.nemerosa.ontrack.kdsl.acceptance.tests.queue

import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import net.nemerosa.ontrack.kdsl.spec.Ontrack
import net.nemerosa.ontrack.kdsl.spec.extension.av.autoVersioning

class QueueACCTestSupport(
    private val ontrack: Ontrack,
    ) {

    fun waitForQueueRecordToBeDone(queueID: String) {
        waitUntil(
            initial = 1_000L,
            timeout = 30_000L,
        ) {
            val record = ontrack.queue.getQueueRecord(queueID)
            record != null && record.done
        }
    }
}