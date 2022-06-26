package net.nemerosa.ontrack.kdsl.acceptance.tests.av

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.kdsl.acceptance.tests.ACCProperties
import net.nemerosa.ontrack.kdsl.acceptance.tests.github.TestOnGitHubPostProcessing
import net.nemerosa.ontrack.kdsl.acceptance.tests.github.gitHubClient
import net.nemerosa.ontrack.kdsl.acceptance.tests.github.system.GitHubRepositoryContext
import org.junit.jupiter.api.Test
import java.util.*

@TestOnGitHubPostProcessing
class ACCAutoVersioningPostProcessing : AbstractACCAutoVersioningTestSupport() {

    @Test
    fun `Auto versioning post processing`() {
        withTestGitHubPostProcessingRepository {
            TODO()
        }
    }

    private fun withTestGitHubPostProcessingRepository(
        code: GitHubRepositoryContext.() -> Unit,
    ) {
        // Unique name for the repository
        val uuid = UUID.randomUUID().toString()
        val repo = "ontrack-auto-versioning-test-$uuid"

        // Forking the sample repository
        gitHubClient.postForLocation(
            "/repos/${ACCProperties.GitHub.AutoVersioning.PostProcessing.sampleOrg}/${ACCProperties.GitHub.AutoVersioning.PostProcessing.sample}/forks",
            mapOf(
                "organization" to ACCProperties.GitHub.organization
            )
        )

        // TODO Waiting until the repository is created

        // Rename the repository
        gitHubClient.patchForObject(
            "/repos/${ACCProperties.GitHub.organization}/${ACCProperties.GitHub.AutoVersioning.PostProcessing.sample}",
            mapOf(
                "name" to repo
            ),
            JsonNode::class.java
        )

        // Working with this repository
        try {

            // Context
            val context = GitHubRepositoryContext(repo)

            // Running the code
            context.code()


        } finally {
            // Deleting the repository
            gitHubClient.delete("/repos/${ACCProperties.GitHub.organization}/$repo")
        }
    }

}