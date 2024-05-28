package net.nemerosa.ontrack.kdsl.acceptance.tests.av

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.kdsl.acceptance.tests.scm.withMockScmRepository
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningProjectProperty
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningSourceConfig
import net.nemerosa.ontrack.kdsl.spec.extension.av.autoVersioningProperty
import net.nemerosa.ontrack.kdsl.spec.extension.av.setAutoVersioningConfig
import org.junit.jupiter.api.Test

class ACCAutoVersioningProjectProperty : AbstractACCAutoVersioningTestSupport() {

    private val ref = Time.now

    @Test
    fun `Auto versioning when no rules`() {
        withAVRulesTest(
            rules = null,
            targetBranches = listOf("release/1", "release/2"),
            avTargetBranches = listOf("release/1", "release/2"),
        )
    }

    @Test
    fun `Auto versioning default inclusion rules`() {
        withAVRulesTest(
            rules = AutoVersioningProjectProperty(),
            targetBranches = listOf("release/1", "release/2"),
            avTargetBranches = listOf("release/1", "release/2"),
        )
    }

    @Test
    fun `Auto versioning inclusion rules`() {
        withAVRulesTest(
            rules = AutoVersioningProjectProperty(
                branchIncludes = listOf("release-2.*"),
            ),
            targetBranches = listOf("release/1", "release/2"),
            avTargetBranches = listOf("release/2"),
        )
    }

    @Test
    fun `Auto versioning exclusion rules`() {
        withAVRulesTest(
            rules = AutoVersioningProjectProperty(
                branchIncludes = listOf("release-.*"),
                branchExcludes = listOf("release-1.*"),
            ),
            targetBranches = listOf("release/1", "release/2"),
            avTargetBranches = listOf("release/2"),
        )
    }

    @Test
    fun `Auto versioning activity rules`() {
        withAVRulesTest(
            rules = AutoVersioningProjectProperty(
                branchIncludes = listOf("release-.*"),
                lastActivityDate = ref.minusDays(14),
            ),
            targetBranches = listOf("release/1", "release/2"),
            targetBranchesAgeDays = mapOf(
                "release/1" to 16,
                "release/2" to 10,
            ),
            avTargetBranches = listOf("release/2"),
        )
    }

    private fun withAVRulesTest(
        rules: AutoVersioningProjectProperty? = null,
        targetBranches: List<String> = listOf("release/1", "release/2"),
        targetBranchesAgeDays: Map<String, Int> = emptyMap(),
        avTargetBranches: List<String>,
    ) {
        withMockScmRepository(ontrack) {
            withAutoVersioning {

                val dependencyBuild = project {
                    branch("main") {
                        promotion(name = "GOLD")
                        build("2") { this }
                    }
                }

                targetBranches.forEach { branch ->
                    repositoryFile("versions.properties", branch) {
                        "version = 1"
                    }
                }

                project {
                    if (rules != null) {
                        autoVersioningProperty = rules
                    }
                    targetBranches.forEach { scmBranch ->
                        val branchName = scmBranch.replace("/", "-")
                        branch(branchName) {
                            configuredForMockRepository(scmBranch)
                            setAutoVersioningConfig(
                                listOf(
                                    AutoVersioningSourceConfig(
                                        sourceProject = dependencyBuild.branch.project.name,
                                        sourceBranch = "main",
                                        sourcePromotion = "GOLD",
                                        targetPath = "versions.properties",
                                        targetProperty = "version",
                                    )
                                )
                            )
                            // Creating a build for the last activity
                            val days = targetBranchesAgeDays[scmBranch]
                            if (days != null) {
                                val timestamp = ref.minusDays(days.toLong())
                                build {
                                    updateCreationTime(
                                        time = timestamp,
                                    )
                                }
                            }
                        }
                    }

                    dependencyBuild.promote("GOLD")

                    waitForAutoVersioningCompletion()

                    assertThatMockScmRepository {
                        targetBranches.forEach { scmBranch ->
                            if (scmBranch in avTargetBranches) {
                                hasPR(
                                    from = "feature/auto-upgrade-${dependencyBuild.branch.project.name}-2-*",
                                    to = scmBranch
                                )
                                fileContains("versions.properties", scmBranch) {
                                    "version = 2"
                                }
                            } else {
                                hasNoPR(to = scmBranch)
                                fileContains("versions.properties", scmBranch) {
                                    "version = 1"
                                }
                            }
                        }
                    }
                }

            }
        }
    }

}