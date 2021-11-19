package net.nemerosa.ontrack.kdsl.acceptance.tests.github.ingestion

import net.nemerosa.ontrack.kdsl.acceptance.annotations.AcceptanceTestSuite
import net.nemerosa.ontrack.kdsl.acceptance.tests.github.AbstractACCDSLGitHubTestSupport
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.resourceAsJson
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import net.nemerosa.ontrack.kdsl.connector.parse
import net.nemerosa.ontrack.kdsl.spec.extension.github.gitHub
import net.nemerosa.ontrack.kdsl.spec.extension.github.ingestion.ingestion
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail

@AcceptanceTestSuite
class ACCDSLGitHubIngestion : AbstractACCDSLGitHubTestSupport() {

    @Test
    fun `Workflow run on the default queue`() {
        // Pre-check: getting the initial counters
        val preMetrics = getMetrics()
        val preRoutingCount =
            preMetrics.getCounter("ontrack_extension_github_ingestion_queue_produced_count", "routing" to "test") ?: 0
        val preDefaultCount = preMetrics.getCounter(
            "ontrack_extension_github_ingestion_queue_consumed_count",
            "queue" to "github.ingestion.default"
        ) ?: 0
        val preRepositoryCount = preMetrics.getCounter(
            "ontrack_extension_github_ingestion_queue_consumed_count",
            "queue" to "github.ingestion.test"
        ) ?: 0

        // Cleanup of existing target project
        ontrack.findProjectByName("test-repository")?.delete()

        // Configuration: create a GitHub configuration
        ontrack.gitHub.createConfig(fakeGitHubConfiguration())

        // Payload: preparing the payload for a test repository
        val payload = resourceAsJson("/github/ingestion/workflow_run.json")
        // Payload: sending the payload for the GitHub configuration")
        val response = rawConnector().post(
            "/hook/secured/github/ingestion",
            headers = mapOf(
                "Content-Type" to "application/json",
                "X-GitHub-Delivery" to UUID.randomUUID().toString(),
                "X-GitHub-Event" to "workflow_run",
                "X-GitHub-Hook-ID" to "123456789",
                "X-GitHub-Hook-Installation-Target-ID" to "1234567890",
                "X-GitHub-Hook-Installation-Target-Type" to "organization",
                "X-Hub-Signature-256" to "signature-is-not-checked",
            ),
            body = payload,
        )
        // Payload: response checks
        assertEquals(200, response.statusCode)
        val payloadUuid = response.body.parse<GitHubIngestionHookResponse>().run {
            assertTrue(processing, "Processing has started")
            assertNotNull(uuid, "The payload has been assigned a UUID")
            // Getting the UUID
            uuid
        }
        // Payload: wait until the payload processing is complete
        waitUntil {
            val processedPayload = ontrack.gitHub.ingestion.payloads(
                uuid = payloadUuid.toString()
            ).items.firstOrNull()
            if (processedPayload != null) {
                // Completed
                // Not completed yet
                when (processedPayload.status) {
                    "COMPLETED" -> true
                    "ERRORED" -> fail("Payload was processed but finished with an error: ${processedPayload.message}")
                    else -> false
                }
            } else {
                false // Not available yet
            }
        }

        // Check: project, branch & build
        assertNotNull(ontrack.findBuildByName("test-repository", "main", "ci-1"), "Build has been created")

        // Post-check: getting the new counters & comparison
        val postMetrics = getMetrics()
        val postRoutingCount =
            postMetrics.getCounter("ontrack_extension_github_ingestion_queue_produced_count", "routing" to "test") ?: 0
        val postDefaultCount = postMetrics.getCounter(
            "ontrack_extension_github_ingestion_queue_consumed_count",
            "queue" to "github.ingestion.default"
        ) ?: 0
        val postRepositoryCount = postMetrics.getCounter(
            "ontrack_extension_github_ingestion_queue_consumed_count",
            "queue" to "github.ingestion.test"
        ) ?: 0
        assertTrue(postRoutingCount > preRoutingCount, "Test routing has been used")
        assertTrue(postRepositoryCount > preRepositoryCount, "Test queue has been used")
        assertEquals(postDefaultCount, preDefaultCount, "Default queue has not been used")
    }

}