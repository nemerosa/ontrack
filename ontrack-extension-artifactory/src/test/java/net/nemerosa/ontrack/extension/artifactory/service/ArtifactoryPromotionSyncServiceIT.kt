package net.nemerosa.ontrack.extension.artifactory.service

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.artifactory.ArtifactoryConfProperties
import net.nemerosa.ontrack.extension.artifactory.client.ArtifactoryClient
import net.nemerosa.ontrack.extension.artifactory.client.ArtifactoryClientFactory
import net.nemerosa.ontrack.extension.artifactory.configuration.ArtifactoryConfiguration
import net.nemerosa.ontrack.extension.artifactory.configuration.ArtifactoryConfigurationService
import net.nemerosa.ontrack.extension.artifactory.model.ArtifactoryStatus
import net.nemerosa.ontrack.extension.artifactory.property.ArtifactoryPromotionSyncProperty
import net.nemerosa.ontrack.extension.artifactory.property.ArtifactoryPromotionSyncPropertyType
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.job.JobRunListener
import net.nemerosa.ontrack.job.orchestrator.JobOrchestratorSupplier
import net.nemerosa.ontrack.test.assertIs
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ArtifactoryPromotionSyncServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var service: ArtifactoryPromotionSyncService

    @Autowired
    private lateinit var configurationService: ArtifactoryConfigurationService

    @Autowired
    private lateinit var artifactoryConfProperties: ArtifactoryConfProperties

    @Autowired // Mock
    private lateinit var artifactoryClient: ArtifactoryClient

    @Configuration
    @Profile(RunProfile.DEV)
    class ArtifactoryPromotionSyncServiceITConfiguration {

        /**
         * Client mock
         */
        @Bean
        fun artifactoryClient() = mockk<ArtifactoryClient>(relaxed = true)

        /**
         * Factory
         */
        @Bean
        @Primary
        fun artifactoryClientFactory(client: ArtifactoryClient) = ArtifactoryClientFactory { client }

    }

    @BeforeEach
    fun setup() {
        // Existing promotions
        every {
            artifactoryClient.getStatuses(any())
        } returns listOf(
            ArtifactoryStatus(
                "COPPER",
                "x",
                Time.now()
            )
        )
    }

    @Test
    fun `Promotions are not cumulative`() {
        project {
            branch {
                val copper = promotionLevel(name = "COPPER")
                val build = build(name = "1.0.0") {
                    promote(copper)
                }
                // Sync with Artifactory
                service.syncBuild(this, "1.0.0", "1.0.0", artifactoryClient, JobRunListener.out())
                // Checks that a new promotion has NOT been created because one exists already
                val promotions = structureService.getPromotionRunsForBuild(build.id)
                    .filter { it.promotionLevel.id() == copper.id() }.size
                assertEquals(1, promotions, "No new promotion has been created")
            }
        }

    }

    @Test
    fun `Creation of a promotion from Artifactory`() {
        project {
            branch {
                val copper = promotionLevel(name = "COPPER")
                val build = build(name = "1.0.0")
                // Sync with Artifactory
                service.syncBuild(this, "1.0.0", "1.0.0", artifactoryClient, JobRunListener.out())
                // Checks that a promotion has been created
                val promotionsNumber = structureService
                    .getPromotionRunsForBuild(build.id)
                    .filter { it.promotionLevel.id() == copper.id() }
                    .size
                assertEquals(1, promotionsNumber, "A promotion has been created")
            }
        }
    }

    @Test
    fun `One sync job per configured branch`() {
        val configuration = ArtifactoryConfiguration("test", "https://artifactory", "user", "password")
        asAdmin {
            configurationService.newConfiguration(configuration)
        }
        project {
            // Unconfigured branch
            val unconfiguredBranch = branch()

            // Configured branch
            branch branch@{
                setProperty(
                    this, ArtifactoryPromotionSyncPropertyType::class.java, ArtifactoryPromotionSyncProperty(
                        configuration,
                        "project",
                        "1.0.*",
                        30
                    )
                )
                // Gets the list of jobs
                assertIs<JobOrchestratorSupplier>(service) {
                    val jobs = it.jobRegistrations.toList()
                    // Unconfigured branch
                    assertTrue(jobs.none { j ->
                        j.job.key.type.category.key == "artifactory" &&
                                j.job.key.type.key == "build-sync" &&
                                j.job.key.id == unconfiguredBranch.id.toString()
                    }, "No job when branch is not configured")
                    // Configured branch
                    val job = jobs.find { j ->
                        j.job.key.type.category.key == "artifactory" &&
                                j.job.key.type.key == "build-sync" &&
                                j.job.key.id == this@branch.id.toString()
                    }
                    assertNotNull(job)
                }
            }
        }
    }

    @Test
    fun `No sync job when disabled`() {
        val configuration = ArtifactoryConfiguration("test", "https://artifactory", "user", "password")
        asAdmin {
            configurationService.newConfiguration(configuration)
        }
        project {
            // Unconfigured branch
            branch()

            // Configured branch
            branch branch@{
                setProperty(
                    this, ArtifactoryPromotionSyncPropertyType::class.java, ArtifactoryPromotionSyncProperty(
                        configuration,
                        "project",
                        "1.0.*",
                        30
                    )
                )
                // Gets the list of jobs
                assertIs<JobOrchestratorSupplier>(service) {
                    val oldState = artifactoryConfProperties.buildSyncDisabled
                    try {
                        artifactoryConfProperties.buildSyncDisabled = true
                        val jobs = it.jobRegistrations.toList()
                        // No job at all
                        assertTrue(jobs.isEmpty(), "No job is created")
                    } finally {
                        artifactoryConfProperties.buildSyncDisabled = oldState
                    }
                }
            }
        }
    }

    @Test
    fun `No sync job when no configuration`() {
        val configuration = ArtifactoryConfiguration("test", "https://artifactory", "user", "password")
        asAdmin {
            configurationService.newConfiguration(configuration)
        }
        project {
            // Unconfigured branch
            branch()

            // Configured branch
            branch branch@{
                setProperty(
                    this, ArtifactoryPromotionSyncPropertyType::class.java, ArtifactoryPromotionSyncProperty(
                        configuration,
                        "project",
                        "1.0.*",
                        30
                    )
                )
                // Gets the list of jobs
                assertIs<JobOrchestratorSupplier>(service) {
                    // Deleting all configurations before checking
                    asAdmin {
                        configurationService.configurations.forEach { conf ->
                            configurationService.deleteConfiguration(conf.name)
                        }
                    }
                    val jobs = it.jobRegistrations.toList()
                    // No job at all
                    assertTrue(jobs.isEmpty(), "No job is created")
                }
            }
        }
    }

}