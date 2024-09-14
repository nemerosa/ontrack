package net.nemerosa.ontrack.kdsl.acceptance.tests.jenkins

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.kdsl.acceptance.tests.av.AbstractACCAutoVersioningTestSupport
import net.nemerosa.ontrack.kdsl.acceptance.tests.scm.withMockScmRepository
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.spec.configurations.configurations
import net.nemerosa.ontrack.kdsl.spec.extension.av.AutoVersioningSourceConfig
import net.nemerosa.ontrack.kdsl.spec.extension.av.setAutoVersioningConfig
import net.nemerosa.ontrack.kdsl.spec.extension.jenkins.JenkinsConfiguration
import net.nemerosa.ontrack.kdsl.spec.extension.jenkins.JenkinsPostProcessingSettings
import net.nemerosa.ontrack.kdsl.spec.extension.jenkins.jenkins
import net.nemerosa.ontrack.kdsl.spec.extension.jenkins.jenkinsPostProcessing
import net.nemerosa.ontrack.kdsl.spec.extension.jenkins.mock.mock
import net.nemerosa.ontrack.kdsl.spec.settings.settings
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ACCJenkinsAutoVersioningPostProcessing : AbstractACCAutoVersioningTestSupport() {

    @Test
    fun `Job and parameters of the Jenkins post-processing configuration are templates`() {
        withMockScmRepository(ontrack) {
            withAutoVersioning {
                repositoryFile("gradle.properties") {
                    "some-version = 1.0.0"
                }
                // Jenkins config
                val jenkinsConfigName = uid("j-")
                ontrack.configurations.jenkins.create(
                    JenkinsConfiguration(
                        name = jenkinsConfigName,
                        url = "http://jenkins",
                    )
                )
                // Jenkins AV config
                ontrack.settings.jenkinsPostProcessing.set(
                    JenkinsPostProcessingSettings(
                        config = jenkinsConfigName,
                        job = "/default/pipeline",
                    )
                )
                // Dependency
                val depBranch = branchWithPromotion(promotion = "RELEASE")
                // Sample project to automatically upgrade & post-process using a custom Jenkins job
                project {
                    branch {
                        configuredForMockRepository()
                        setAutoVersioningConfig(
                            listOf(
                                AutoVersioningSourceConfig(
                                    sourceProject = depBranch.project.name,
                                    sourceBranch = depBranch.name,
                                    sourcePromotion = "RELEASE",
                                    targetPath = "gradle.properties",
                                    targetProperty = "some-version",
                                    postProcessing = "jenkins",
                                    postProcessingConfig = mapOf(
                                        "dockerImage" to "sample/docker",
                                        "dockerCommand" to "sample command",
                                        "job" to "/custom/pipeline/${'$'}{targetBranch.scmBranch}",
                                        "parameters" to listOf(
                                            mapOf(
                                                "name" to "PROMOTION",
                                                "value" to "${'$'}{PROMOTION}",
                                            )
                                        )
                                    ).asJson(),
                                )
                            )
                        )

                        // Creates a new promoted version of the dependency
                        depBranch.apply {
                            build(name = "2.0.0") {
                                promote("RELEASE")
                            }
                        }

                        waitForAutoVersioningCompletion()

                        assertThatMockScmRepository {
                            fileContains("gradle.properties") {
                                "some-version = 2.0.0"
                            }
                            // Checks the Jenkins execution
                            val jenkinsJob = ontrack.jenkins.mock.job(jenkinsConfigName, "/custom/pipeline/main").jenkinsJob
                            assertNotNull(jenkinsJob, "Custom pipeline found") { job ->
                                assertTrue(job.wasCalled, "Custom pipeline was called")
                                assertEquals("RELEASE", job.lastBuild.parameters["PROMOTION"])
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun `Extra parameters and specific job`() {
        withMockScmRepository(ontrack) {
            withAutoVersioning {
                repositoryFile("gradle.properties") {
                    "some-version = 1.0.0"
                }
                // Jenkins config
                val jenkinsConfigName = uid("j-")
                ontrack.configurations.jenkins.create(
                    JenkinsConfiguration(
                        name = jenkinsConfigName,
                        url = "http://jenkins",
                    )
                )
                // Jenkins AV config
                ontrack.settings.jenkinsPostProcessing.set(
                    JenkinsPostProcessingSettings(
                        config = jenkinsConfigName,
                        job = "/default/pipeline",
                    )
                )
                // Dependency
                val depBranch = branchWithPromotion(promotion = "RELEASE")
                // Sample project to automatically upgrade & post-process using a custom Jenkins job
                project {
                    branch {
                        configuredForMockRepository()
                        setAutoVersioningConfig(
                            listOf(
                                AutoVersioningSourceConfig(
                                    sourceProject = depBranch.project.name,
                                    sourceBranch = depBranch.name,
                                    sourcePromotion = "RELEASE",
                                    targetPath = "gradle.properties",
                                    targetProperty = "some-version",
                                    postProcessing = "jenkins",
                                    postProcessingConfig = mapOf(
                                        "dockerImage" to "sample/docker",
                                        "dockerCommand" to "sample command",
                                        "job" to "/custom/pipeline",
                                        "parameters" to listOf(
                                            mapOf(
                                                "name" to "EXTRA",
                                                "value" to "some-value",
                                            )
                                        )
                                    ).asJson(),
                                )
                            )
                        )

                        // Creates a new promoted version of the dependency
                        depBranch.apply {
                            build(name = "2.0.0") {
                                promote("RELEASE")
                            }
                        }

                        waitForAutoVersioningCompletion()

                        assertThatMockScmRepository {
                            fileContains("gradle.properties") {
                                "some-version = 2.0.0"
                            }
                            // Checks the Jenkins execution
                            val jenkinsJob = ontrack.jenkins.mock.job(jenkinsConfigName, "/custom/pipeline").jenkinsJob
                            assertNotNull(jenkinsJob, "Custom pipeline found") { job ->
                                assertTrue(job.wasCalled, "Custom pipeline was called")
                                assertEquals("some-value", job.lastBuild.parameters["EXTRA"])
                            }
                        }
                    }
                }
            }
        }
    }

}