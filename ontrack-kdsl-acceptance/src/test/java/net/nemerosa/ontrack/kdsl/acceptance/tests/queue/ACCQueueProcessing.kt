package net.nemerosa.ontrack.kdsl.acceptance.tests.queue

import net.nemerosa.ontrack.kdsl.acceptance.tests.notifications.AbstractACCDSLNotificationsTestSupport
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.spec.extension.queue.QueueRecordState
import net.nemerosa.ontrack.kdsl.spec.extension.queue.queue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ACCQueueProcessing : AbstractACCDSLNotificationsTestSupport() {

    private val queueSupport = QueueACCTestSupport(ontrack)

    @Test
    fun `Tracking of the messages on the queue`() {
        val message = uid("msg_")
        val id = queueSupport.postMockMessage(message)
        // Waits for the processing to be completed
        queueSupport.waitForQueueRecordToBe(id, QueueRecordState.COMPLETED)
        // Gets the records and checks the queueName
        assertNotNull(ontrack.queue.findQueueRecordByID(id), "Queue record present")
    }

    @Test
    fun `Queue records have a source`() {
        val message = uid("msg_")
        val id = queueSupport.postMockMessage(message)
        // Waits for the processing to be completed
        queueSupport.waitForQueueRecordToBe(id, QueueRecordState.COMPLETED)
        // Gets the records and checks the queueName
        assertNotNull(ontrack.queue.findQueueRecordByID(id), "Queue record present") { record ->
            assertNotNull(record.source, "Queue source is set") { source ->
                assertEquals("queue", source.feature)
                assertEquals("post", source.id)
            }
        }
    }

}