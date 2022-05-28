package net.nemerosa.ontrack.kdsl.acceptance.tests.github.ingestion

import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.kdsl.acceptance.annotations.AcceptanceTestSuite
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.resourceAsText
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.spec.extension.general.setLabel
import net.nemerosa.ontrack.kdsl.spec.extension.github.gitHub
import net.nemerosa.ontrack.kdsl.spec.extension.github.ingestion.ingestion
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

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
            val links = build.getLinksUsing().items
            assertEquals(
                mapOf(
                    component to "1.0.0",
                    library to "321",
                ),
                links.associate {
                    it.branch.project.name to it.name
                }
            )
        }
    }

    /**
     * Tests the ingestion of build links, with the build being identified by name
     *
     * The scenario plays as follows:
     *
     * * creating two target projects for the links
     * * creating a build
     * * calling the API to set some validation data (simulating a call by GHA)
     */
    @Test
    fun `Ingestion of build links by name`() {
        // Targets
        val component = uid("p")
        val library = uid("p")
        ontrack.createProject(component, "").createBranch("main", "").createBuild("1.0.0", "")
        ontrack.createProject(library, "").createBranch("main", "").createBuild("321", "").setLabel("2.0.0")

        // A unique repository name
        val repository = uid("r")

        // Creating a project, a branch and a build
        val build = ontrack
            .createProject(repository, "")
            .createBranch("main", "")
            .createBuild("build-15", "")

        // Calling the API to simulate the GHA call
        val linksUuid = ontrack.gitHub.ingestion.buildLinksByBuildName(
            owner = "nemerosa",
            repository = repository,
            buildName = "build-15",
            addOnly = false,
            buildLinks = mapOf(
                component to "1.0.0", // Build by name
                library to "#2.0.0",  // Build by label
            )
        )

        // At the end, waits for all payloads to be processed
        waitUntilPayloadIsProcessed(linksUuid)

        // Checks that the build has been created & contains the correct links
        val links = build.getLinksUsing().items
        assertEquals(
            mapOf(
                component to "1.0.0",
                library to "321",
            ),
            links.associate {
                it.branch.project.name to it.name
            }
        )
    }

    /**
     * Tests the ingestion of build links, with the build being identified by name
     *
     * The scenario plays as follows:
     *
     * * creating two target projects for the links
     * * creating a build
     * * creating some existing links
     * * calling the API to set some validation data (simulating a call by GHA)
     */
    @Test
    fun `Ingestion of build links by name with add only`() {
        // Targets
        val component = uid("p")
        val library = uid("p")
        ontrack.createProject(component, "").createBranch("main", "").createBuild("1.0.0", "")
        ontrack.createProject(library, "").createBranch("main", "").createBuild("321", "").setLabel("2.0.0")

        // Existing link
        val existing = uid("p")
        ontrack.createProject(existing, "").createBranch("main", "").createBuild("10.0.0", "")

        // A unique repository name
        val repository = uid("r")

        // Creating a project, a branch and a build
        val build = ontrack
            .createProject(repository, "")
            .createBranch("main", "")
            .createBuild("build-15", "")

        // Add links
        build.linksTo(mapOf(
            existing to "10.0.0",
        ))

        // Calling the API to simulate the GHA call
        val linksUuid = ontrack.gitHub.ingestion.buildLinksByBuildName(
            owner = "nemerosa",
            repository = repository,
            buildName = "build-15",
            addOnly = true,
            buildLinks = mapOf(
                component to "1.0.0", // Build by name
                library to "#2.0.0",  // Build by label
            )
        )

        // At the end, waits for all payloads to be processed
        waitUntilPayloadIsProcessed(linksUuid)

        // Checks that the build has been created & contains the correct links
        val links = build.getLinksUsing().items
        assertEquals(
            mapOf(
                component to "1.0.0",
                library to "321",
                existing to "10.0.0",
            ),
            links.associate {
                it.branch.project.name to it.name
            }
        )
    }

    /**
     * Tests the ingestion of build links, with the build being identified by label
     *
     * The scenario plays as follows:
     *
     * * creating two target projects for the links
     * * creating a build
     * * calling the API to set some validation data (simulating a call by GHA)
     */
    @Test
    fun `Ingestion of build links by label`() {
        // Targets
        val component = uid("p")
        val library = uid("p")
        ontrack.createProject(component, "").createBranch("main", "").createBuild("1.0.0", "")
        ontrack.createProject(library, "").createBranch("main", "").createBuild("321", "").setLabel("2.0.0")

        // A unique repository name
        val repository = uid("r")

        // Creating a project, a branch and a build
        val build = ontrack
            .createProject(repository, "")
            .createBranch("main", "")
            .createBuild("build-15", "")
            .setLabel("3.0.0")

        // Calling the API to simulate the GHA call
        val linksUuid = ontrack.gitHub.ingestion.buildLinksByBuildLabel(
            owner = "nemerosa",
            repository = repository,
            buildLabel = "3.0.0",
            buildLinks = mapOf(
                component to "1.0.0", // Build by name
                library to "#2.0.0",  // Build by label
            )
        )

        // At the end, waits for all payloads to be processed
        waitUntilPayloadIsProcessed(linksUuid)

        // Checks that the build has been created & contains the correct links
        val links = build.getLinksUsing().items
        assertEquals(
            mapOf(
                component to "1.0.0",
                library to "321",
            ),
            links.associate {
                it.branch.project.name to it.name
            }
        )
    }

}