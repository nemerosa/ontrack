package net.nemerosa.ontrack.kdsl.acceptance.tests.github.ingestion

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.kdsl.acceptance.annotations.AcceptanceTestSuite
import net.nemerosa.ontrack.kdsl.acceptance.tests.github.AbstractACCDSLGitHubTestSupport
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.resourceAsText
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import net.nemerosa.ontrack.kdsl.connector.parse
import net.nemerosa.ontrack.kdsl.spec.extension.github.GitHubConfiguration
import net.nemerosa.ontrack.kdsl.spec.extension.github.gitHub
import net.nemerosa.ontrack.kdsl.spec.extension.github.ingestion.GitHubIngestionValidationDataInput
import net.nemerosa.ontrack.kdsl.spec.extension.github.ingestion.ingestion
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail

@AcceptanceTestSuite
class ACCDSLGitHubIngestionValidateData : AbstractACCDSLGitHubTestSupport() {

    /**
     * Tests the ingestion of validation data.
     *
     * The scenario plays as follows:
     *
     * * starting a workflow run
     * * completing a step in a job which creates a validation
     * * calling the API to set some validation data (simulating a call by GHA)
     */
    @Test
    fun `Ingestion of validation data`() {

        // A unique repository name
        val repository = uid("r")
        // A run ID
        val runId = 2270870720L

        // Configuration: create a GitHub configuration
        val gitHubConfiguration = fakeGitHubConfiguration()
        ontrack.gitHub.createConfig(gitHubConfiguration)

        // Starting a workflow run
        val workflowRunRequestedPayload =
            resourceAsText("/github/ingestion/validate-data/workflow_run_started.json")
                .replace("#repository", repository)
                .replace("#runId", runId.toString())
                .parseAsJson()
        val workflowRunRequestedUuid =
            sendPayloadToHook(gitHubConfiguration, "workflow_run", workflowRunRequestedPayload)

        // Completing a step in a job
        val workflowJobStepCompletedPayload =
            resourceAsText("/github/ingestion/validate-data/workflow_job_step_completed.json")
                .replace("#repository", repository)
                .replace("#runId", runId.toString())
                .parseAsJson()
        val workflowJobStepCompletedUuid =
            sendPayloadToHook(gitHubConfiguration, "workflow_job", workflowJobStepCompletedPayload)

        // Calling the API to simulate the GHA call
        val validateDataUuid = ontrack.gitHub.ingestion.validateDataByRunId(
            owner = "nemerosa",
            repository = repository,
            runId = runId,
            validation = "validation-to-target",
            validationData = GitHubIngestionValidationDataInput(
                type = "net.nemerosa.ontrack.extension.general.validation.MetricsValidationDataType",
                data = mapOf(
                    "metrics" to mapOf(
                        "position" to 4.5,
                        "speed" to 0.7,
                        "acceleration" to 1.0,
                    )
                ).asJson()
            ),
            validationStatus = "PASSED"
        )

        // At the end, waits for all payloads to be processed
        waitUntilPayloadIsProcessed(workflowRunRequestedUuid)
        waitUntilPayloadIsProcessed(workflowJobStepCompletedUuid)
        waitUntilPayloadIsProcessed(validateDataUuid)

        // Checks that the run has been created & contains the right data
        assertNotNull(ontrack.findBuildByName(repository, "main", "ci-1"), "Build has been created") { build ->
            assertNotNull(build.getValidationRuns("validation-to-target").firstOrNull(),
                "Validation run has been created") { run ->
                assertEquals("PASSED", run.lastStatus.id)
                assertNotNull(run.data, "Validation data has been set") { data ->
                    assertEquals("net.nemerosa.ontrack.extension.general.validation.MetricsValidationDataType",
                        data.type)
                    assertEquals(
                        mapOf(
                            "metrics" to mapOf(
                                "position" to 4.5,
                                "speed" to 0.7,
                                "acceleration" to 1.0,
                            )
                        ).asJson(),
                        data.data
                    )
                }
            }
        }
    }

    private fun sendPayloadToHook(
        gitHubConfiguration: GitHubConfiguration,
        event: String,
        payload: JsonNode,
    ): UUID {
        val response = rawConnector().post(
            "/hook/secured/github/ingestion?configuration=${gitHubConfiguration.name}",
            headers = mapOf(
                "Content-Type" to "application/json",
                "X-GitHub-Delivery" to UUID.randomUUID().toString(),
                "X-GitHub-Event" to event,
                "X-GitHub-Hook-ID" to "123456789",
                "X-GitHub-Hook-Installation-Target-ID" to "1234567890",
                "X-GitHub-Hook-Installation-Target-Type" to "organization",
                "X-Hub-Signature-256" to "signature-is-not-checked",
            ),
            body = payload,
        )
        // Payload: response checks
        assertEquals(200, response.statusCode)
        // Extracting the payload processing UUIS
        return response.body.parse<GitHubIngestionHookResponse>().run {
            assertTrue(processing, "Processing has started")
            assertNotNull(uuid, "The payload has been assigned a UUID")
            // Getting the UUID
            uuid
        }
    }

    private fun waitUntilPayloadIsProcessed(
        payloadUuid: UUID,
    ) {
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
    }

}