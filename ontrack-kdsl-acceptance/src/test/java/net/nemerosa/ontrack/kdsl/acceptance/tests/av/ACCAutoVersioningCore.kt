package net.nemerosa.ontrack.kdsl.acceptance.tests.av

import net.nemerosa.ontrack.kdsl.acceptance.tests.github.TestOnGitHubPlayground
import net.nemerosa.ontrack.kdsl.acceptance.tests.github.system.withTestGitHubRepository
import org.junit.jupiter.api.Test

@TestOnGitHubPlayground
class ACCAutoVersioningCore : AbstractACCAutoVersioningTestSupport() {

    @Test
    fun `Auto versioning on promotion`() {
        withTestGitHubRepository {
            withAutoVersioning {
                repositoryFile("gradle.properties") {
                    """
                        # Some comment
                        some-property = some-value
                        some-version = 1.0.0
                    """.trimIndent()
                }
                val dependency = branchWithPromotion(promotion = "IRON")
                project {
                    branch {
                        configuredForGitHubRepository(ontrack)
                        configuredForAutoVersioning(
                            sourceProject = dependency.project.name,
                            sourceBranch = dependency.name,
                            targetPath = "gradle.properties",
                            targetProperty = "some-version",
                            sourcePromotion = "IRON",
                        )

                        dependency.apply {
                            build(name = "2.0.0") {
                                promote("IRON")
                            }
                        }

                        waitForAutoVersioningCompletion()

                        assertThatGitHubRepository {
                            hasPR(
                                from = "feature/auto-upgrade-${dependency.project.name}-2.0.0-fad58de7366495db4650cfefac2fcd61",
                                to = "main"
                            )
                            fileContains("gradle.properties") {
                                """
                                    # Some comment
                                    some-property = some-value
                                    some-version = 2.0.0
                                """.trimIndent()
                            }
                        }

                    }
                }
            }
        }
    }

    @Test
    fun `Auto versioning on promotion for a release branch`() {
        withTestGitHubRepository {
            withAutoVersioning {
                repositoryFile("gradle.properties") {
                    """
                        # Some comment
                        some-property = some-value
                        some-version = 1.0.0
                    """.trimIndent()
                }
                val dependency = branchWithPromotion(name = "release-1.0", promotion = "IRON")
                project {
                    branch {
                        configuredForGitHubRepository(ontrack)
                        configuredForAutoVersioning(
                            sourceProject = dependency.project.name,
                            sourceBranch = dependency.name,
                            targetPath = "gradle.properties",
                            targetProperty = "some-version",
                            sourcePromotion = "IRON",
                        )

                        dependency.apply {
                            build(name = "2.0.0") {
                                promote("IRON")
                            }
                        }

                        waitForAutoVersioningCompletion()

                        assertThatGitHubRepository {
                            hasPR(
                                from = "feature/auto-upgrade-${dependency.project.name}-2.0.0-fad58de7366495db4650cfefac2fcd61",
                                to = "main"
                            )
                            fileContains("gradle.properties") {
                                """
                                    # Some comment
                                    some-property = some-value
                                    some-version = 2.0.0
                                """.trimIndent()
                            }
                        }

                    }
                }
            }
        }
    }

    @Test
    fun `Auto versioning on promotion for a release branch identified using Git`() {
        withTestGitHubRepository {
            withAutoVersioning {
                repositoryFile("gradle.properties") {
                    """
                        # Some comment
                        some-property = some-value
                        some-version = 1.0.0
                    """.trimIndent()
                }
                val dependency = branchWithPromotion(name = "release-1.0", promotion = "IRON") {
                    configuredForGitHubRepository(ontrack, "release/1.0")
                }
                project {
                    branch {
                        configuredForGitHubRepository(ontrack)
                        configuredForAutoVersioning(
                            sourceProject = dependency.project.name,
                            sourceBranch = "release/1.0",
                            targetPath = "gradle.properties",
                            targetProperty = "some-version",
                            sourcePromotion = "IRON",
                        )

                        dependency.apply {
                            build(name = "2.0.0") {
                                promote("IRON")
                            }
                        }

                        waitForAutoVersioningCompletion()

                        assertThatGitHubRepository {
                            hasPR(
                                from = "feature/auto-upgrade-${dependency.project.name}-2.0.0-fad58de7366495db4650cfefac2fcd61",
                                to = "main"
                            )
                            fileContains("gradle.properties") {
                                """
                                    # Some comment
                                    some-property = some-value
                                    some-version = 2.0.0
                                """.trimIndent()
                            }
                        }

                    }
                }
            }
        }
    }

    @Test
    fun `Auto versioning on promotion with empty post processing`() {
        withTestGitHubRepository {
            withAutoVersioning {
                repositoryFile("gradle.properties") {
                    """
                        # Some comment
                        some-property = some-value
                        some-version = 1.0.0
                    """.trimIndent()
                }
                val dependency = branchWithPromotion(promotion = "IRON")
                project {
                    branch {
                        configuredForGitHubRepository(ontrack)
                        configuredForAutoVersioning(
                            sourceProject = dependency.project.name,
                            sourceBranch = dependency.name,
                            targetPath = "gradle.properties",
                            targetProperty = "some-version",
                            sourcePromotion = "IRON",
                            postProcessing = "",
                        )

                        dependency.apply {
                            build(name = "2.0.0") {
                                promote("IRON")
                            }
                        }

                        waitForAutoVersioningCompletion()

                        assertThatGitHubRepository {
                            hasPR(
                                from = "feature/auto-upgrade-${dependency.project.name}-2.0.0-fad58de7366495db4650cfefac2fcd61",
                                to = "main"
                            )
                            fileContains("gradle.properties") {
                                """
                                    # Some comment
                                    some-property = some-value
                                    some-version = 2.0.0
                                """.trimIndent()
                            }
                        }

                    }
                }
            }
        }
    }

    @Test
    fun `Auto versioning not requested if feature disabled`() {
        withTestGitHubRepository {
            withAutoVersioning(enabled = false) {
                repositoryFile("gradle.properties") {
                    """
                        some-version = 1.0.0
                    """.trimIndent()
                }
                val dependency = branchWithPromotion(promotion = "IRON")
                project {
                    branch {
                        configuredForGitHubRepository(ontrack)
                        configuredForAutoVersioning(
                            sourceProject = dependency.project.name,
                            sourceBranch = dependency.name,
                            targetPath = "gradle.properties",
                            targetProperty = "some-version",
                            sourcePromotion = "IRON",
                            postProcessing = "",
                        )

                        dependency.apply {
                            build(name = "2.0.0") {
                                promote("IRON")
                            }
                        }

                        waitForAutoVersioningCompletion()

                        assertThatGitHubRepository {

                            hasNoBranch("feature/auto-upgrade-${dependency.project.name}-2.0.0-fad58de7366495db4650cfefac2fcd61")

                            fileContains("gradle.properties") {
                                """
                                    some-version = 1.0.0
                                """.trimIndent()
                            }

                            hasNoPR(to = "main")

                        }

                    }
                }
            }
        }
    }

    @Test
    fun `Auto versioning not requested if no match on branch`() {
        withTestGitHubRepository {
            withAutoVersioning {
                repositoryFile("gradle.properties") {
                    """
                        some-version = 1.0.0
                    """.trimIndent()
                }
                val dependency = branchWithPromotion(promotion = "IRON")
                val other = branchWithPromotion(promotion = "IRON")
                project {
                    branch {
                        configuredForGitHubRepository(ontrack)
                        configuredForAutoVersioning(
                            sourceProject = other.project.name,
                            sourceBranch = other.name,
                            targetPath = "gradle.properties",
                            targetProperty = "some-version",
                            sourcePromotion = "IRON",
                            postProcessing = "",
                        )

                        dependency.apply {
                            build(name = "2.0.0") {
                                promote("IRON")
                            }
                        }

                        waitForAutoVersioningCompletion()

                        assertThatGitHubRepository {

                            hasNoBranch("feature/auto-upgrade-${dependency.project.name}-2.0.0-fad58de7366495db4650cfefac2fcd61")

                            fileContains("gradle.properties") {
                                """
                                    some-version = 1.0.0
                                """.trimIndent()
                            }

                            hasNoPR(to = "main")

                        }

                    }
                }
            }
        }
    }

    @Test
    fun `Auto versioning not requested if no match on promotion`() {
        withTestGitHubRepository {
            withAutoVersioning {
                repositoryFile("gradle.properties") {
                    """
                        some-version = 1.0.0
                    """.trimIndent()
                }
                val dependency = project {
                    branch(name = "main") {
                        promotion(name = "IRON")
                        promotion(name = "SILVER")
                        this
                    }
                }
                project {
                    branch {
                        configuredForGitHubRepository(ontrack)
                        configuredForAutoVersioning(
                            sourceProject = dependency.project.name,
                            sourceBranch = dependency.name,
                            targetPath = "gradle.properties",
                            targetProperty = "some-version",
                            sourcePromotion = "SILVER",
                            postProcessing = "",
                        )

                        dependency.apply {
                            build(name = "2.0.0") {
                                promote("IRON")
                            }
                        }

                        waitForAutoVersioningCompletion()

                        assertThatGitHubRepository {

                            hasNoBranch("feature/auto-upgrade-${dependency.project.name}-2.0.0-fad58de7366495db4650cfefac2fcd61")

                            fileContains("gradle.properties") {
                                """
                                    some-version = 1.0.0
                                """.trimIndent()
                            }

                            hasNoPR(to = "main")

                        }

                    }
                }
            }
        }
    }

    @Test
    fun `Auto versioning on promotion using regex for property`() {
        withTestGitHubRepository {
            withAutoVersioning {
                repositoryFile("package.json") {
                    """
                        "my-version":    "^1.0.0",
                    """.trimIndent()
                }
                val dependency = branchWithPromotion(promotion = "IRON")
                project {
                    branch {
                        configuredForGitHubRepository(ontrack)
                        configuredForAutoVersioning(
                            sourceProject = dependency.project.name,
                            sourceBranch = dependency.name,
                            targetPath = "package.json",
                            targetRegex = """"my-version"\s*:\s*"\^(.*)",""",
                            sourcePromotion = "IRON",
                        )

                        dependency.apply {
                            build(name = "2.0.0") {
                                promote("IRON")
                            }
                        }

                        waitForAutoVersioningCompletion()

                        assertThatGitHubRepository {
                            hasPR(
                                from = "feature/auto-upgrade-${dependency.project.name}-2.0.0-fad58de7366495db4650cfefac2fcd61",
                                to = "main"
                            )
                            fileContains("package.json") {
                                """
                                    "my-version":    "^2.0.0",
                                """.trimIndent()
                            }
                        }

                    }
                }
            }
        }
    }

    @Test
    fun `Auto versioning on promotion with explicit auto approval`() {
        withTestGitHubRepository {
            withAutoVersioning {
                repositoryFile("gradle.properties") {
                    """
                        # Some comment
                        some-property = some-value
                        some-version = 1.0.0
                    """.trimIndent()
                }
                val dependency = branchWithPromotion(promotion = "IRON")
                project {
                    branch {
                        configuredForGitHubRepository(ontrack)
                        configuredForAutoVersioning(
                            sourceProject = dependency.project.name,
                            sourceBranch = dependency.name,
                            targetPath = "gradle.properties",
                            targetProperty = "some-version",
                            sourcePromotion = "IRON",
                            autoApproval = true,
                        )

                        dependency.apply {
                            build(name = "2.0.0") {
                                promote("IRON")
                            }
                        }

                        waitForAutoVersioningCompletion()

                        assertThatGitHubRepository {
                            hasPR(
                                from = "feature/auto-upgrade-${dependency.project.name}-2.0.0-fad58de7366495db4650cfefac2fcd61",
                                to = "main"
                            )
                            fileContains("gradle.properties") {
                                """
                                    # Some comment
                                    some-property = some-value
                                    some-version = 2.0.0
                                """.trimIndent()
                            }
                        }

                    }
                }
            }
        }
    }

    @Test
    fun `Auto versioning on promotion with explicit NO auto approval`() {
        withTestGitHubRepository {
            withAutoVersioning {
                repositoryFile("gradle.properties") {
                    """
                        # Some comment
                        some-property = some-value
                        some-version = 1.0.0
                    """.trimIndent()
                }
                val dependency = branchWithPromotion(promotion = "IRON")
                project {
                    branch {
                        configuredForGitHubRepository(ontrack)
                        configuredForAutoVersioning(
                            sourceProject = dependency.project.name,
                            sourceBranch = dependency.name,
                            targetPath = "gradle.properties",
                            targetProperty = "some-version",
                            sourcePromotion = "IRON",
                            autoApproval = false,
                        )

                        dependency.apply {
                            build(name = "2.0.0") {
                                promote("IRON")
                            }
                        }

                        waitForAutoVersioningCompletion()

                        assertThatGitHubRepository {
                            val pr = hasPR(
                                from = "feature/auto-upgrade-${dependency.project.name}-2.0.0-fad58de7366495db4650cfefac2fcd61",
                                to = "main"
                            )
                            checkPRIsNotApproved(pr)
                        }

                    }
                }
            }
        }
    }

    @Test
    fun `Auto versioning on promotion with custom upgrade branch with custom project`() {
        withTestGitHubRepository {
            withAutoVersioning {
                repositoryFile("gradle.properties") {
                    """
                        # Some comment
                        some-property = some-value
                        some-version = 1.0.0
                    """.trimIndent()
                }
                val dependency = branchWithPromotion(promotion = "IRON")
                project {
                    branch {
                        configuredForGitHubRepository(ontrack)
                        configuredForAutoVersioning(
                            sourceProject = dependency.project.name,
                            sourceBranch = dependency.name,
                            targetPath = "gradle.properties",
                            targetProperty = "some-version",
                            sourcePromotion = "IRON",
                            upgradeBranchPattern = "feature/upgrade-test-AAA-<version>",
                        )

                        dependency.apply {
                            build(name = "2.0.0") {
                                promote("IRON")
                            }
                        }

                        waitForAutoVersioningCompletion()

                        assertThatGitHubRepository {
                            hasPR(
                                from = "feature/upgrade-test-AAA-2.0.0",
                                to = "main"
                            )
                            fileContains("gradle.properties") {
                                """
                                    # Some comment
                                    some-property = some-value
                                    some-version = 2.0.0
                                """.trimIndent()
                            }
                        }

                    }
                }
            }
        }
    }

    @Test
    fun `Auto versioning on promotion with custom upgrade branch with default project`() {
        withTestGitHubRepository {
            withAutoVersioning {
                repositoryFile("gradle.properties") {
                    """
                        # Some comment
                        some-property = some-value
                        some-version = 1.0.0
                    """.trimIndent()
                }
                val dependency = branchWithPromotion(promotion = "IRON")
                project {
                    branch {
                        configuredForGitHubRepository(ontrack)
                        configuredForAutoVersioning(
                            sourceProject = dependency.project.name,
                            sourceBranch = dependency.name,
                            targetPath = "gradle.properties",
                            targetProperty = "some-version",
                            sourcePromotion = "IRON",
                            upgradeBranchPattern = "feature/upgrade-test-${dependency.project.name}-2.0.0",
                        )

                        dependency.apply {
                            build(name = "2.0.0") {
                                promote("IRON")
                            }
                        }

                        waitForAutoVersioningCompletion()

                        assertThatGitHubRepository {
                            hasPR(
                                from = "feature/upgrade-test-AAA-2.0.0",
                                to = "main"
                            )
                            fileContains("gradle.properties") {
                                """
                                    # Some comment
                                    some-property = some-value
                                    some-version = 2.0.0
                                """.trimIndent()
                            }
                        }

                    }
                }
            }
        }
    }

    @Test
    fun `Auto versioning not requested if branch not configured for Git`() {
        withTestGitHubRepository {
            withAutoVersioning {
                repositoryFile("gradle.properties") {
                    """
                        # Some comment
                        some-property = some-value
                        some-version = 1.0.0
                    """.trimIndent()
                }
                val dependency = branchWithPromotion(promotion = "IRON")
                project {
                    branch {
                        project.configuredForGitHub(ontrack)
                        // configuredForGitHubRepository(ontrack) Only the project, not the branch
                        configuredForAutoVersioning(
                            sourceProject = dependency.project.name,
                            sourceBranch = dependency.name,
                            targetPath = "gradle.properties",
                            targetProperty = "some-version",
                            sourcePromotion = "IRON",
                        )

                        dependency.apply {
                            build(name = "2.0.0") {
                                promote("IRON")
                            }
                        }

                        waitForAutoVersioningCompletion()

                        assertThatGitHubRepository {

                            hasNoBranch("feature/auto-upgrade-${dependency.project.name}-2.0.0-fad58de7366495db4650cfefac2fcd61")

                            fileContains("gradle.properties") {
                                """
                                    some-version = 1.0.0
                                """.trimIndent()
                            }

                            hasNoPR(to = "main")
                        }

                    }
                }
            }
        }
    }

}