package net.nemerosa.ontrack.extension.github.ingestion.ui

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.github.ingestion.IngestionHookFixtures
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayload
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayloadStatus
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayloadStorage
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.getRequiredTextField
import net.nemerosa.ontrack.json.parse
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class GQLRootQueryGitHubIngestionHookPayloadsIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var ingestionHookPayloadStorage: IngestionHookPayloadStorage

    @Test
    fun `Getting a payload by UUID`() {
        val payload = IngestionHookFixtures.sampleWorkflowRunIngestionPayload(
            message = "Sample payload",
        )
        ingestionHookPayloadStorage.store(payload)
        run("""
            {
             gitHubIngestionHookPayloads(uuid: "${payload.uuid}") {
                pageItems {
                    uuid
                    timestamp
                    gitHubDelivery
                    gitHubEvent
                    gitHubHookID
                    gitHubHookInstallationTargetID
                    gitHubHookInstallationTargetType
                    payload
                    status
                    started
                    message
                    completion
                }
             }
            }
        """).let { data ->
            val item = data.path("gitHubIngestionHookPayloads").path("pageItems").first().parse<IngestionHookPayload>()
            assertEquals(payload.uuid, item.uuid)
        }
    }

    @Test
    fun `Getting the list of payloads`() {
        testPayloads(
            expected = (40 downTo 21).toList(),
        )
    }

    @Test
    fun `Paginating the list of payloads`() {
        testPayloads(
            offset = 20,
            size = 10,
            expected = (20 downTo 11).toList(),
        )
    }

    @Test
    fun `Filtering the list of payloads on a status`() {
        testPayloads(
            size = 5,
            statuses = listOf(IngestionHookPayloadStatus.ERRORED),
            expected = listOf(39, 35, 31, 27, 23)
        )
    }

    @Test
    fun `Filtering the list of payloads on several statuses`() {
        testPayloads(
            size = 5,
            statuses = listOf(IngestionHookPayloadStatus.ERRORED, IngestionHookPayloadStatus.COMPLETED),
            expected = listOf(39, 38, 35, 34, 31)
        )
    }

    private fun testPayloads(
        offset: Int? = null,
        size: Int? = null,
        statuses: List<IngestionHookPayloadStatus>? = null,
        expected: List<Int>,
    ) {
        createPayloads()
        run(
            """
                query Payloads(
                    ${'$'}offset: Int,
                    ${'$'}size: Int,
                    ${'$'}statuses: [IngestionHookPayloadStatus!],
                ) {
                    gitHubIngestionHookPayloads(
                        offset: ${'$'}offset,
                        size: ${'$'}size,
                        statuses: ${'$'}statuses,
                    ) {
                        pageItems {
                            message
                        }
                    }
                }
            """, mapOf(
                "offset" to offset,
                "size" to size,
                "statuses" to statuses,
            )
        ).let { data ->
            assertEquals(
                expected.map { no ->
                    "Payload #$no"
                },
                data.path("gitHubIngestionHookPayloads").path("pageItems").map { node ->
                    node.getRequiredTextField("message")
                }
            )
        }
    }

    private fun createPayloads() {
        ingestionHookPayloadStorage.cleanUntil(Time.now())
        val payloads = (1..40).map { no ->
            val status = when (no % 4) {
                0 -> IngestionHookPayloadStatus.SCHEDULED
                1 -> IngestionHookPayloadStatus.PROCESSING
                2 -> IngestionHookPayloadStatus.COMPLETED
                else -> IngestionHookPayloadStatus.ERRORED
            }
            IngestionHookFixtures.sampleWorkflowRunIngestionPayload(
                message = "Payload #$no",
                status = status,
            )
        }
        payloads.forEach { payload ->
            ingestionHookPayloadStorage.store(payload)
        }
    }

}