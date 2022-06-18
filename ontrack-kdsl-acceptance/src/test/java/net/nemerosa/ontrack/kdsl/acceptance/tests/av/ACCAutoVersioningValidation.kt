package net.nemerosa.ontrack.kdsl.acceptance.tests.av

import net.nemerosa.ontrack.kdsl.acceptance.tests.github.TestOnGitHubPlayground
import net.nemerosa.ontrack.kdsl.acceptance.tests.github.system.withTestGitHubRepository
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningSourceConfig
import net.nemerosa.ontrack.kdsl.spec.extension.av.autoVersioningCheck
import net.nemerosa.ontrack.kdsl.spec.extension.av.setAutoVersioningConfig
import net.nemerosa.ontrack.kdsl.spec.extension.general.AutoValidationStampProperty
import net.nemerosa.ontrack.kdsl.spec.extension.general.autoValidationStampProperty
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@TestOnGitHubPlayground
class ACCAutoVersioningValidation : AbstractACCAutoVersioningTestSupport() {

    @Test
    fun `Auto versioning check reporting OK using the source code`() {
        withTestGitHubRepository {
            withAutoVersioning {

                repositoryFile("gradle.properties") {
                    "dependencyVersion = 1.0.0"
                }

                val dependency = branchWithPromotion(promotion = "IRON")
                dependency.apply {
                    build(name = "1.0.0") {
                        promote("IRON")
                    }
                }

                project {
                    branch {
                        configuredForGitHubRepository(ontrack)
                        setAutoVersioningConfig(
                            listOf(
                                AutoVersioningSourceConfig(
                                    sourceProject = dependency.project.name,
                                    sourceBranch = dependency.name,
                                    sourcePromotion = "IRON",
                                    targetPath = "gradle.properties",
                                    targetProperty = "dependencyVersion",
                                    validationStamp = "custom-validation",
                                )
                            )
                        )
                        autoValidationStampProperty = AutoValidationStampProperty(autoCreateIfNotPredefined = true)

                        build("2.0.0") {

                            autoVersioningCheck()

                            val runs = getValidationRuns("custom-validation")
                            assertNotNull(runs.firstOrNull()) {
                                assertEquals("PASSED", it.lastStatus.id)
                            }

                        }

                    }
                }
            }
        }
    }

    @Test
    fun `Auto versioning check reporting NOT OK using the source code`() {
        withTestGitHubRepository {
            withAutoVersioning {

                repositoryFile("gradle.properties") {
                    "dependencyVersion = 1.0.0"
                }

                val dependency = branchWithPromotion(promotion = "IRON")
                dependency.apply {
                    build(name = "1.1.0") {
                        promote("IRON")
                    }
                }

                project {
                    branch {
                        configuredForGitHubRepository(ontrack)
                        setAutoVersioningConfig(
                            listOf(
                                AutoVersioningSourceConfig(
                                    sourceProject = dependency.project.name,
                                    sourceBranch = dependency.name,
                                    sourcePromotion = "IRON",
                                    targetPath = "gradle.properties",
                                    targetProperty = "dependencyVersion",
                                    validationStamp = "custom-validation",
                                )
                            )
                        )
                        autoValidationStampProperty = AutoValidationStampProperty(autoCreateIfNotPredefined = true)

                        build("2.0.0") {

                            autoVersioningCheck()

                            val runs = getValidationRuns("custom-validation")
                            assertNotNull(runs.firstOrNull()) {
                                assertEquals("FAILED", it.lastStatus.id)
                            }

                        }

                    }
                }
            }
        }
    }

    @Test
    fun `Auto versioning check reporting OK using the source code using auto mode`() {
        withTestGitHubRepository {
            withAutoVersioning {

                repositoryFile("gradle.properties") {
                    "dependencyVersion = 1.0.0"
                }

                val dependency = branchWithPromotion(promotion = "IRON")
                dependency.apply {
                    build(name = "1.0.0") {
                        promote("IRON")
                    }
                }

                project {
                    branch {
                        configuredForGitHubRepository(ontrack)
                        setAutoVersioningConfig(
                            listOf(
                                AutoVersioningSourceConfig(
                                    sourceProject = dependency.project.name,
                                    sourceBranch = dependency.name,
                                    sourcePromotion = "IRON",
                                    targetPath = "gradle.properties",
                                    targetProperty = "dependencyVersion",
                                    validationStamp = "auto",
                                )
                            )
                        )
                        autoValidationStampProperty = AutoValidationStampProperty(autoCreateIfNotPredefined = true)

                        build("2.0.0") {

                            autoVersioningCheck()

                            val runs = getValidationRuns("auto-versioning-${dependency.project.name}")
                            assertNotNull(runs.firstOrNull()) {
                                assertEquals("PASSED", it.lastStatus.id)
                            }

                        }

                    }
                }
            }
        }
    }

    @Test
    fun `Auto versioning check reporting OK using build links`() {
        withTestGitHubRepository {
            withAutoVersioning {

                repositoryFile("gradle.properties") {
                    "dependencyVersion = 1.0.0"
                }

                val dependency = branchWithPromotion(promotion = "IRON")
                dependency.apply {
                    build(name = "1.0.0") {
                        promote("IRON")
                    }
                }

                project {
                    branch {
                        configuredForGitHubRepository(ontrack)
                        setAutoVersioningConfig(
                            listOf(
                                AutoVersioningSourceConfig(
                                    sourceProject = dependency.project.name,
                                    sourceBranch = dependency.name,
                                    sourcePromotion = "IRON",
                                    targetPath = "gradle.properties",
                                    targetProperty = "dependencyVersion",
                                    validationStamp = "custom-validation",
                                )
                            )
                        )
                        autoValidationStampProperty = AutoValidationStampProperty(autoCreateIfNotPredefined = true)

                        build("2.0.0") {

                            linksTo(dependency.project.name to "1.0.0")

                            autoVersioningCheck()

                            val runs = getValidationRuns("custom-validation")
                            assertNotNull(runs.firstOrNull()) {
                                assertEquals("PASSED", it.lastStatus.id)
                            }

                        }

                    }
                }
            }
        }
    }

    @Test
    fun `Auto versioning check reporting NOT OK using build links`() {
        withTestGitHubRepository {
            withAutoVersioning {

                repositoryFile("gradle.properties") {
                    "dependencyVersion = 1.0.0"
                }

                val dependency = branchWithPromotion(promotion = "IRON")
                dependency.apply {
                    build(name = "1.0.0") {
                        promote("IRON")
                    }
                    build(name = "1.1.0") {
                        promote("IRON")
                    }
                }

                project {
                    branch {
                        configuredForGitHubRepository(ontrack)
                        setAutoVersioningConfig(
                            listOf(
                                AutoVersioningSourceConfig(
                                    sourceProject = dependency.project.name,
                                    sourceBranch = dependency.name,
                                    sourcePromotion = "IRON",
                                    targetPath = "gradle.properties",
                                    targetProperty = "dependencyVersion",
                                    validationStamp = "custom-validation",
                                )
                            )
                        )
                        autoValidationStampProperty = AutoValidationStampProperty(autoCreateIfNotPredefined = true)

                        build("2.0.0") {

                            linksTo(dependency.project.name to "1.0.0")

                            autoVersioningCheck()

                            val runs = getValidationRuns("custom-validation")
                            assertNotNull(runs.firstOrNull()) {
                                assertEquals("FAILED", it.lastStatus.id)
                            }

                        }

                    }
                }
            }
        }
    }

    @Test
    fun `Auto versioning check reporting OK using build links and auto mode`() {
        withTestGitHubRepository {
            withAutoVersioning {

                repositoryFile("gradle.properties") {
                    "dependencyVersion = 1.0.0"
                }

                val dependency = branchWithPromotion(promotion = "IRON")
                dependency.apply {
                    build(name = "1.0.0") {
                        promote("IRON")
                    }
                }

                project {
                    branch {
                        configuredForGitHubRepository(ontrack)
                        setAutoVersioningConfig(
                            listOf(
                                AutoVersioningSourceConfig(
                                    sourceProject = dependency.project.name,
                                    sourceBranch = dependency.name,
                                    sourcePromotion = "IRON",
                                    targetPath = "gradle.properties",
                                    targetProperty = "dependencyVersion",
                                    validationStamp = "auto",
                                )
                            )
                        )
                        autoValidationStampProperty = AutoValidationStampProperty(autoCreateIfNotPredefined = true)

                        build("2.0.0") {

                            linksTo(dependency.project.name to "1.0.0")

                            autoVersioningCheck()

                            val runs = getValidationRuns("auto-versioning-${dependency.project.name}")
                            assertNotNull(runs.firstOrNull()) {
                                assertEquals("PASSED", it.lastStatus.id)
                            }

                        }

                    }
                }
            }
        }
    }

}