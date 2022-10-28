package net.nemerosa.ontrack.kdsl.acceptance.tests.av

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.kdsl.acceptance.tests.ACCProperties
import net.nemerosa.ontrack.kdsl.acceptance.tests.AbstractACCDSLTestSupport
import net.nemerosa.ontrack.kdsl.acceptance.tests.github.gitHubClient
import net.nemerosa.ontrack.kdsl.acceptance.tests.github.system.GitHubRepositoryContext
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import net.nemerosa.ontrack.kdsl.spec.Branch
import net.nemerosa.ontrack.kdsl.spec.Project
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningSettings
import net.nemerosa.ontrack.kdsl.spec.extension.av.autoVersioning
import net.nemerosa.ontrack.kdsl.spec.settings.settings
import java.util.*

abstract class AbstractACCAutoVersioningTestSupport : AbstractACCDSLTestSupport() {

    protected fun withAutoVersioning(
        enabled: Boolean = true,
        code: () -> Unit,
    ) {
        val old = ontrack.settings.autoVersioning.get()
        try {
            ontrack.settings.autoVersioning.set(
                AutoVersioningSettings(
                    enabled = enabled,
                )
            )
            code()
        } finally {
            ontrack.settings.autoVersioning.set(old)
        }
    }

    protected fun branchWithPromotion(
        name: String = "main",
        promotion: String,
        code: Branch.() -> Unit = {},
    ): Branch = project {
        branch(name = name) {
            promotion(name = promotion)
            code()
            this
        }
    }

    protected fun waitForAutoVersioningCompletion(
        timeout: Long = 60_000L,
    ) {
        waitUntil(
            initial = 1_000L,
            timeout = timeout,
        ) {
            ontrack.autoVersioning.stats.pendingOrders == 0
        }
    }

    protected fun checkMostRecentStateOfAutoVersioningAuditForSourceAndTargetBranch(
        sourceProject: Project,
        targetBranch: Branch,
        expectedMostRecentState: String?,
        expectedData: Map<String, String> = emptyMap(),
    ) {
        waitUntil(
            task = "Latest state for ${sourceProject.name}/${targetBranch.name} is $expectedMostRecentState",
            initial = 1_000L,
            timeout = 120_000L,
        ) {
            // Check result
            val entry = ontrack.autoVersioning.audit.entries(
                size = 1,
                source = sourceProject.name,
                project = targetBranch.project.name,
                branch = targetBranch.name
            ).firstOrNull()
            entry?.let {
                // Checks its most recent state
                if (expectedMostRecentState != null && expectedMostRecentState == it.mostRecentState.state) {
                    // Checks the data
                    if (expectedData.isNotEmpty()) {
                        expectedData.all { (key, valueRegex) ->
                            val actualValue: JsonNode? = it.mostRecentState.data[key]
                            actualValue != null && actualValue.asText().matches(valueRegex.toRegex())
                        }
                    } else {
                        true
                    }
                } else {
                    false
                }
            } ?: false
        }
    }

    protected fun withTestGitHubPostProcessingRepository(
        code: GitHubRepositoryContext.() -> Unit,
    ) {
        // Unique name for the repository
        val uuid = UUID.randomUUID().toString()
        val repo = "ontrack-auto-versioning-test-$uuid"

        // Forking the sample repository
        gitHubClient.postForLocation(
            "/repos/${ACCProperties.GitHub.AutoVersioning.PostProcessing.Sample.org}/${ACCProperties.GitHub.AutoVersioning.PostProcessing.Sample.repository}/forks",
            mapOf(
                "organization" to ACCProperties.GitHub.organization
            )
        )

        // Rename the repository
        gitHubClient.patchForObject(
            "/repos/${ACCProperties.GitHub.organization}/${ACCProperties.GitHub.AutoVersioning.PostProcessing.Sample.repository}",
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