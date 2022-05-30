package net.nemerosa.ontrack.kdsl.acceptance.tests.github.ingestion

import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.kdsl.acceptance.annotations.AcceptanceTestSuite
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.resourceAsText
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.spec.extension.general.label
import net.nemerosa.ontrack.kdsl.spec.extension.git.gitCommitProperty
import net.nemerosa.ontrack.kdsl.spec.extension.github.gitHub
import net.nemerosa.ontrack.kdsl.spec.extension.github.ingestion.setBranchGitHubIngestionConfig
import org.junit.Test
import kotlin.test.assertEquals

@AcceptanceTestSuite
class ACCDSLGitHubIngestionTagging : AbstractACCDSLGitHubIngestionTestSupport() {

    /**
     * Tests the tagging based on the commit property
     *
     * Scenario:
     *
     * * ingestion configuration based on defaults
     * * create a build with the commit property
     * * send a tag payload whose commit targets the build
     * * checks the build has been labelled
     */
    @Test
    fun `Tagging on commit property by default`() {
        val projectName = uid("p")
        val project = ontrack.createProject(projectName, "")
        val branch = project.createBranch("main", "")

        // Configuration: create a GitHub configuration
        val gitHubConfiguration = fakeGitHubConfiguration()
        ontrack.gitHub.createConfig(gitHubConfiguration)

        // Build
        val build = branch.createBuild("1", "").apply {
            gitCommitProperty = "tag-commit"
        }

        // Pushing the ingestion config
        branch.setBranchGitHubIngestionConfig("")

        // Tag event
        val pushTagPayload =
            resourceAsText("/github/ingestion/tagging/push_tag.json")
                .replace("#repository", projectName)
                .replace("#ref", "refs/tags/2.1.0")
                .replace("#head_commit", "tag-commit")
                .replace("#base_ref", "refs/heads/${branch.name}")
                .parseAsJson()
        val pushTagPayloadUuid = sendPayloadToHook(gitHubConfiguration, "push", pushTagPayload)

        // At the end, waits for all payloads to be processed
        waitUntilPayloadIsProcessed(pushTagPayloadUuid)

        // Checks the promoted build is labelled with the tag
        assertEquals("2.1.0", build.label)

    }

    /**
     * Tests the tagging based on the latest promotion.
     *
     * Scenario:
     *
     * * ingestion configuration based on promotion
     * * create a promoted build
     * * create an extra build
     * * send a tag payload whose commit targets the latest build
     * * checks the promoted build has been labelled
     */
    @Test
    fun `Tagging on promotion`() {
        val projectName = uid("p")
        val project = ontrack.createProject(projectName, "")
        val branch = project.createBranch("main", "")
        branch.createPromotionLevel("BRONZE", "")

        // Configuration: create a GitHub configuration
        val gitHubConfiguration = fakeGitHubConfiguration()
        ontrack.gitHub.createConfig(gitHubConfiguration)

        // Promoted build
        val promoted = branch.createBuild("1", "").apply {
            promote("BRONZE")
            gitCommitProperty = "any-commit"
        }

        // Build targeted by the tag
        val annotated = branch.createBuild("2", "").apply {
            gitCommitProperty = "tag-commit"
        }

        // Pushing the ingestion config
        branch.setBranchGitHubIngestionConfig(
            """
                tagging:
                    strategies:
                        - type: promotion
                          config:
                            name: BRONZE
            """
        )

        // Tag event
        val pushTagPayload =
            resourceAsText("/github/ingestion/tagging/push_tag.json")
                .replace("#repository", projectName)
                .replace("#ref", "refs/tags/2.1.0")
                .replace("#head_commit", "tag-commit")
                .replace("#base_ref", "refs/heads/${branch.name}")
                .parseAsJson()
        val pushTagPayloadUuid = sendPayloadToHook(gitHubConfiguration, "push", pushTagPayload)

        // At the end, waits for all payloads to be processed
        waitUntilPayloadIsProcessed(pushTagPayloadUuid)

        // Checks the promoted build is labelled with the tag
        assertEquals("2.1.0", promoted.label)
        assertEquals(null, annotated.label)

    }

    /**
     * Tests the tagging based on the latest promotion with no prior tag commit
     *
     * Scenario:
     *
     * * ingestion configuration based on promotion
     * * create a promoted build
     * * create an extra build
     * * send a tag payload whose commit is not referred to by Ontrack
     * * checks the promoted build has been labelled
     */
    @Test
    fun `Tagging on promotion without prior commit`() {
        val projectName = uid("p")
        val project = ontrack.createProject(projectName, "")
        val branch = project.createBranch("main", "")
        branch.createPromotionLevel("BRONZE", "")

        // Configuration: create a GitHub configuration
        val gitHubConfiguration = fakeGitHubConfiguration()
        ontrack.gitHub.createConfig(gitHubConfiguration)

        // Promoted build
        val promoted = branch.createBuild("1", "").apply {
            promote("BRONZE")
            gitCommitProperty = "any-commit"
        }

        // Build targeted by the tag
        val newest = branch.createBuild("2", "")

        // Pushing the ingestion config
        branch.setBranchGitHubIngestionConfig(
            """
                tagging:
                    strategies:
                        - type: promotion
                          config:
                            name: BRONZE
            """
        )

        // Tag event
        val pushTagPayload =
            resourceAsText("/github/ingestion/tagging/push_tag.json")
                .replace("#repository", projectName)
                .replace("#ref", "refs/tags/2.1.0")
                .replace("#head_commit", "tag-commit")
                .replace("#base_ref", "refs/heads/${branch.name}")
                .parseAsJson()
        val pushTagPayloadUuid = sendPayloadToHook(gitHubConfiguration, "push", pushTagPayload)

        // At the end, waits for all payloads to be processed
        waitUntilPayloadIsProcessed(pushTagPayloadUuid)

        // Checks the promoted build is labelled with the tag
        assertEquals("2.1.0", promoted.label)
        assertEquals(null, newest.label)

    }

    /**
     * Tests the tagging based on the latest promotion with no prior tag commit and no default strategy
     *
     * Scenario:
     *
     * * ingestion configuration based on promotion
     * * create a promoted build
     * * create an extra build
     * * send a tag payload whose commit is not referred to by Ontrack
     * * checks the promoted build has been labelled
     */
    @Test
    fun `Tagging on promotion without prior commit and no default strategy`() {
        val projectName = uid("p")
        val project = ontrack.createProject(projectName, "")
        val branch = project.createBranch("main", "")
        branch.createPromotionLevel("BRONZE", "")

        // Configuration: create a GitHub configuration
        val gitHubConfiguration = fakeGitHubConfiguration()
        ontrack.gitHub.createConfig(gitHubConfiguration)

        // Promoted build
        val promoted = branch.createBuild("1", "").apply {
            promote("BRONZE")
            gitCommitProperty = "any-commit"
        }

        // Build targeted by the tag
        val newest = branch.createBuild("2", "")

        // Pushing the ingestion config
        branch.setBranchGitHubIngestionConfig(
            """
                tagging:
                    commit-property: false
                    strategies:
                        - type: promotion
                          config:
                            name: BRONZE
            """
        )

        // Tag event
        val pushTagPayload =
            resourceAsText("/github/ingestion/tagging/push_tag.json")
                .replace("#repository", projectName)
                .replace("#ref", "refs/tags/2.1.0")
                .replace("#head_commit", "tag-commit")
                .replace("#base_ref", "refs/heads/${branch.name}")
                .parseAsJson()
        val pushTagPayloadUuid = sendPayloadToHook(gitHubConfiguration, "push", pushTagPayload)

        // At the end, waits for all payloads to be processed
        waitUntilPayloadIsProcessed(pushTagPayloadUuid)

        // Checks the promoted build is labelled with the tag
        assertEquals("2.1.0", promoted.label)
        assertEquals(null, newest.label)

    }

}