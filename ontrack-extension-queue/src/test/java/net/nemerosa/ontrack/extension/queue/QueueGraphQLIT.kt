package net.nemerosa.ontrack.extension.queue

import net.nemerosa.ontrack.extension.queue.record.QueueRecordState
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.json.getRequiredTextField
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class QueueGraphQLIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var queueTestSupport: QueueTestSupport

    @Test
    fun `Querying by ID`() {
        asAdmin {
            // Creating a few items in the queue records
            queueTestSupport.record()
            val candidate = queueTestSupport.record()
            queueTestSupport.record()
            // Looking for the candidate using its ID
            run("""
                    {
                        queueRecordings(filter: {id: "${candidate.id}"}) {
                            pageItems {
                                id
                            }
                        }
                    }
            """) { data ->
                val items = data.path("queueRecordings").path("pageItems")
                assertEquals(1, items.size())
                val item = items.first()
                assertEquals(candidate.id, item.getRequiredTextField("id"))
            }
        }
    }

    @Test
    fun `Querying by state`() {
        asAdmin {
            // Creating a few items in the queue records
            queueTestSupport.record()
            val candidate = queueTestSupport.record(state = QueueRecordState.PROCESSING)
            queueTestSupport.record()
            // Looking for the candidate using its ID
            run("""
                    {
                        queueRecordings(filter: {state: PROCESSING}) {
                            pageItems {
                                id
                            }
                        }
                    }
            """) { data ->
                val items = data.path("queueRecordings").path("pageItems")
                assertEquals(1, items.size())
                val item = items.first()
                assertEquals(candidate.id, item.getRequiredTextField("id"))
            }
        }
    }

    @Test
    fun `Querying by processor`() {
        asAdmin {
            // Creating a few items in the queue records
            queueTestSupport.record()
            val candidate = queueTestSupport.record(processor = "xxxx")
            queueTestSupport.record()
            // Looking for the candidate using its ID
            run("""
                    {
                        queueRecordings(filter: {processor: "xxxx"}) {
                            pageItems {
                                id
                            }
                        }
                    }
            """) { data ->
                val items = data.path("queueRecordings").path("pageItems")
                assertEquals(1, items.size())
                val item = items.first()
                assertEquals(candidate.id, item.getRequiredTextField("id"))
            }
        }
    }

}