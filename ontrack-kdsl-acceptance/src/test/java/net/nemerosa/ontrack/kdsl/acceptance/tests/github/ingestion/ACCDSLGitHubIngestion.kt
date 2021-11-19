package net.nemerosa.ontrack.kdsl.acceptance.tests.github.ingestion

import net.nemerosa.ontrack.kdsl.acceptance.annotations.AcceptanceTestSuite
import net.nemerosa.ontrack.kdsl.acceptance.tests.github.AbstractACCDSLGitHubTestSupport
import net.nemerosa.ontrack.kdsl.acceptance.tests.metrics.MetricCollection
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
        runTest(
            name = "default-routing-repository",
            payloadResourcePath = "/github/ingestion/workflow_run_default_routing.json",
            expectedRouting = "default",
            nonExpectedRouting = "test",
        )
    }

    @Test
    fun `Workflow run on the test queue`() {
        runTest(
            name = "test-routing-repository",
            payloadResourcePath = "/github/ingestion/workflow_run_test_routing.json",
            expectedRouting = "test",
            nonExpectedRouting = "default",
        )
    }

    fun runTest(
        name: String,
        payloadResourcePath: String,
        expectedRouting: String,
        nonExpectedRouting: String,
    ) {
        // Pre-check: getting the initial counters
        val preMetrics = getMetrics()

        // Cleanup of existing target project
        ontrack.findProjectByName(name)?.delete()

        // Configuration: create a GitHub configuration
        val gitHubConfiguration = fakeGitHubConfiguration()
        ontrack.gitHub.createConfig(gitHubConfiguration)

        // Payload: preparing the payload for a test repository
        val payload = resourceAsJson(payloadResourcePath)
        // Payload: sending the payload for the GitHub configuration")
        val response = rawConnector().post(
            "/hook/secured/github/ingestion?configuration=${gitHubConfiguration.name}",
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
        assertNotNull(ontrack.findBuildByName(name, "main", "ci-1"), "Build has been created")

        // Post-check: getting the new counters & comparison
        val postMetrics = getMetrics()
        checkNotUsed(preMetrics, postMetrics, nonExpectedRouting)
        checkUsed(preMetrics, postMetrics, expectedRouting)
    }

    private fun checkUsed(preMetrics: MetricCollection, postMetrics: MetricCollection, key: String) {
        checkProducedUsed(preMetrics, postMetrics, key)
        checkConsumeUsed(preMetrics, postMetrics, key)
    }

    private fun checkProducedUsed(preMetrics: MetricCollection, postMetrics: MetricCollection, key: String) {
        val preCount = preMetrics.getProducedCount(key)
        val postCount = postMetrics.getProducedCount(key)
        assertTrue(postCount > preCount, "$key routing has been used.")
    }

    private fun checkConsumeUsed(preMetrics: MetricCollection, postMetrics: MetricCollection, key: String) {
        val preCount = preMetrics.getConsumedCount(key)
        val postCount = postMetrics.getConsumedCount(key)
        assertTrue(postCount > preCount, "$key queue has been used.")
    }

    private fun checkNotUsed(preMetrics: MetricCollection, postMetrics: MetricCollection, key: String) {
        checkProducedNotUsed(preMetrics, postMetrics, key)
        checkConsumeNotUsed(preMetrics, postMetrics, key)
    }

    private fun checkProducedNotUsed(preMetrics: MetricCollection, postMetrics: MetricCollection, key: String) {
        val preCount = preMetrics.getProducedCount(key)
        val postCount = postMetrics.getProducedCount(key)
        assertEquals(preCount, postCount, "$key routing has not been used.")
    }

    private fun checkConsumeNotUsed(preMetrics: MetricCollection, postMetrics: MetricCollection, key: String) {
        val preCount = preMetrics.getConsumedCount(key)
        val postCount = postMetrics.getConsumedCount(key)
        assertEquals(preCount, postCount, "$key queue has not been used.")
    }

    private fun MetricCollection.getProducedCount(routing: String): Int =
        getCounter(
            "ontrack_extension_github_ingestion_queue_produced_count_total",
            "routing" to routing
        ) ?: 0

    private fun MetricCollection.getConsumedCount(queue: String): Int =
        getCounter(
            "ontrack_extension_github_ingestion_queue_consumed_count_total",
            "queue" to "github.ingestion.$queue"
        ) ?: 0

}