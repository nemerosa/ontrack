package net.nemerosa.ontrack.kdsl.acceptance.tests.github.ingestion

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.resourceAsText
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.spec.extension.general.label
import net.nemerosa.ontrack.kdsl.spec.extension.github.gitHub
import net.nemerosa.ontrack.kdsl.spec.extension.github.ingestion.GitHubIngestionValidationDataInput
import net.nemerosa.ontrack.kdsl.spec.extension.github.ingestion.ingestion
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ACCDSLGitHubIngestionValidateData : AbstractACCDSLGitHubIngestionTestSupport() {

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
                    "metrics" to listOf(
                        mapOf(
                            "name" to "position",
                            "value" to 4.5,
                        ),
                        mapOf(
                            "name" to "speed",
                            "value" to 0.7,
                        ),
                        mapOf(
                            "name" to "acceleration",
                            "value" to 1.0,
                        ),
                    ),
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
            assertNotNull(
                build.getValidationRuns("validation-to-target").firstOrNull(),
                "Validation run has been created"
            ) { run ->
                assertEquals("PASSED", run.lastStatus.id)
                assertNotNull(run.data, "Validation data has been set") { data ->
                    assertEquals(
                        "net.nemerosa.ontrack.extension.general.validation.MetricsValidationDataType",
                        data.type
                    )
                    assertEquals(4.5, data.data.path("metrics").path("position").asDouble())
                    assertEquals(0.7, data.data.path("metrics").path("speed").asDouble())
                    assertEquals(1.0, data.data.path("metrics").path("acceleration").asDouble())
                }
            }
        }
    }

    /**
     * Tests the ingestion of validation data using a build name
     *
     * The scenario plays as follows:
     *
     * * creates a build
     * * calling the API to set some validation data using the build name
     */
    @Test
    fun `Ingestion of validation data using a build name`() {
        // A unique repository name, acting also as a project name
        val repository = uid("r")

        // Creating a project, a branch and a build
        val build = ontrack
            .createProject(repository, "")
            .createBranch("main", "")
            .createBuild("build-15", "")

        // Calling the API to simulate the GHA call
        val validateDataUuid = ontrack.gitHub.ingestion.validateDataByBuildName(
            owner = "nemerosa",
            repository = repository,
            buildName = build.name,
            validation = "validation-to-target",
            validationData = GitHubIngestionValidationDataInput(
                type = "net.nemerosa.ontrack.extension.general.validation.MetricsValidationDataType",
                data = mapOf(
                    "metrics" to listOf(
                        mapOf(
                            "name" to "position",
                            "value" to 4.5,
                        ),
                        mapOf(
                            "name" to "speed",
                            "value" to 0.7,
                        ),
                        mapOf(
                            "name" to "acceleration",
                            "value" to 1.0,
                        ),
                    ),
                ).asJson()
            ),
            validationStatus = "PASSED"
        )

        // At the end, waits for all payloads to be processed
        waitUntilPayloadIsProcessed(validateDataUuid)

        // Checks that the run has been created & contains the right data
        assertNotNull(
            build.getValidationRuns("validation-to-target").firstOrNull(),
            "Validation run has been created"
        ) { run ->
            assertEquals("PASSED", run.lastStatus.id)
            assertNotNull(run.data, "Validation data has been set") { data ->
                assertEquals(
                    "net.nemerosa.ontrack.extension.general.validation.MetricsValidationDataType",
                    data.type
                )
                assertEquals(4.5, data.data.path("metrics").path("position").asDouble())
                assertEquals(0.7, data.data.path("metrics").path("speed").asDouble())
                assertEquals(1.0, data.data.path("metrics").path("acceleration").asDouble())
            }
        }
    }

    /**
     * Tests the ingestion of validation data using a build label
     *
     * The scenario plays as follows:
     *
     * * creates a build
     * * setting a label to the build
     * * calling the API to set some validation data using the build label
     */
    @Test
    fun `Ingestion of validation data using a build label`() {
        // A unique repository name, acting also as a project name
        val repository = uid("r")

        // Creating a project, a branch and a build
        val build = ontrack
            .createProject(repository, "")
            .createBranch("main", "")
            .createBuild("build-15", "").apply {
                label = "1.0.0"
            }

        // Calling the API to simulate the GHA call
        val validateDataUuid = ontrack.gitHub.ingestion.validateDataByBuildLabel(
            owner = "nemerosa",
            repository = repository,
            buildLabel = "1.0.0",
            validation = "validation-to-target",
            validationData = GitHubIngestionValidationDataInput(
                type = "net.nemerosa.ontrack.extension.general.validation.MetricsValidationDataType",
                data = mapOf(
                    "metrics" to listOf(
                        mapOf(
                            "name" to "position",
                            "value" to 4.5,
                        ),
                        mapOf(
                            "name" to "speed",
                            "value" to 0.7,
                        ),
                        mapOf(
                            "name" to "acceleration",
                            "value" to 1.0,
                        ),
                    ),
                ).asJson()
            ),
            validationStatus = "PASSED"
        )

        // At the end, waits for all payloads to be processed
        waitUntilPayloadIsProcessed(validateDataUuid)

        // Checks that the run has been created & contains the right data
        assertNotNull(
            build.getValidationRuns("validation-to-target").firstOrNull(),
            "Validation run has been created"
        ) { run ->
            assertEquals("PASSED", run.lastStatus.id)
            assertNotNull(run.data, "Validation data has been set") { data ->
                assertEquals(
                    "net.nemerosa.ontrack.extension.general.validation.MetricsValidationDataType",
                    data.type
                )
                assertEquals(4.5, data.data.path("metrics").path("position").asDouble())
                assertEquals(0.7, data.data.path("metrics").path("speed").asDouble())
                assertEquals(1.0, data.data.path("metrics").path("acceleration").asDouble())
            }
        }
    }

}