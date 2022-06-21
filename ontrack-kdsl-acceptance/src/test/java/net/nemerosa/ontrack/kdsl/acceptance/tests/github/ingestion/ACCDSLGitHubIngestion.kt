package net.nemerosa.ontrack.kdsl.acceptance.tests.github.ingestion

import net.nemerosa.ontrack.kdsl.acceptance.tests.github.AbstractACCDSLGitHubTestSupport
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.resourceAsJson
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import net.nemerosa.ontrack.kdsl.connector.parse
import net.nemerosa.ontrack.kdsl.spec.extension.github.gitHub
import net.nemerosa.ontrack.kdsl.spec.extension.github.ingestion.ingestion
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail

class ACCDSLGitHubIngestion : AbstractACCDSLGitHubTestSupport() {

    @Test
    fun `Workflow run on the default queue`() {
        runTestForQueue(
            name = "default-routing-repository",
            payloadResourcePath = "/github/ingestion/workflow_run_default_routing.json",
            expectedRouting = "default",
        )
    }

    @Test
    fun `Workflow run on the test queue`() {
        runTestForQueue(
            name = "test-routing-repository",
            payloadResourcePath = "/github/ingestion/workflow_run_test_routing.json",
            expectedRouting = "test",
        )
    }

    fun runTestForQueue(
        name: String,
        payloadResourcePath: String,
        expectedRouting: String,
    ) {

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
                when (processedPayload.status) {
                    "COMPLETED" -> {
                        assertEquals(expectedRouting, processedPayload.routing)
                        true
                    }
                    "ERRORED" -> fail("Payload was processed but finished with an error: ${processedPayload.message}")
                    else -> false
                }
            } else {
                false // Not available yet
            }
        }

        // Check: project, branch & build
        assertNotNull(ontrack.findBuildByName(name, "main", "ci-1"), "Build has been created")
    }

}