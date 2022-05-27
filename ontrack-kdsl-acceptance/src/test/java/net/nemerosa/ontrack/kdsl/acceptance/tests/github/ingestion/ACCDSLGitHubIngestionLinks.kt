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
import net.nemerosa.ontrack.kdsl.spec.extension.general.setLabel
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
class ACCDSLGitHubIngestionLinks : AbstractACCDSLGitHubIngestionTestSupport() {

    /**
     * Tests the ingestion of build links.
     *
     * The scenario plays as follows:
     *
     * * creating two target projects for the links
     * * starting a workflow run which creates a build
     * * calling the API to set some validation data (simulating a call by GHA)
     */
    @Test
    fun `Ingestion of build links`() {
        // Targets
        val component = uid("p")
        val library = uid("p")
        ontrack.createProject(component, "").createBranch("main", "").createBuild("1.0.0", "")
        ontrack.createProject(library, "").createBranch("main", "").createBuild("321", "").setLabel("2.0.0")

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

        // Calling the API to simulate the GHA call
        val linksUuid = ontrack.gitHub.ingestion.buildLinksByRunId(
            owner = "nemerosa",
            repository = repository,
            runId = runId,
            buildLinks = mapOf(
                component to "1.0.0", // Build by name
                library to "#2.0.0",  // Build by label
            )
        )

        // At the end, waits for all payloads to be processed
        waitUntilPayloadIsProcessed(workflowRunRequestedUuid)
        waitUntilPayloadIsProcessed(linksUuid)

        // Checks that the build has been created & contains the correct links
        assertNotNull(ontrack.findBuildByName(repository, "main", "ci-1"), "Build has been created") { build ->
            TODO("Gets and paginates the build links")
        }
    }

}