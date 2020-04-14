package net.nemerosa.ontrack.extension.artifactory.service

import com.nhaarman.mockitokotlin2.*
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.artifactory.ArtifactoryConfProperties
import net.nemerosa.ontrack.extension.artifactory.ArtifactoryExtensionFeature
import net.nemerosa.ontrack.extension.artifactory.client.ArtifactoryClient
import net.nemerosa.ontrack.extension.artifactory.client.ArtifactoryClientFactory
import net.nemerosa.ontrack.extension.artifactory.configuration.ArtifactoryConfigurationService
import net.nemerosa.ontrack.extension.artifactory.model.ArtifactoryStatus
import net.nemerosa.ontrack.extension.artifactory.property.ArtifactoryPromotionSyncProperty
import net.nemerosa.ontrack.extension.artifactory.property.ArtifactoryPromotionSyncPropertyType
import net.nemerosa.ontrack.job.JobRunProgress
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.structure.Branch.Companion.of
import net.nemerosa.ontrack.model.structure.Build.Companion.of
import net.nemerosa.ontrack.model.structure.ID.Companion.of
import net.nemerosa.ontrack.model.structure.Project.Companion.of
import net.nemerosa.ontrack.model.structure.PromotionLevel.Companion.of
import net.nemerosa.ontrack.model.structure.PromotionRun.Companion.of
import net.nemerosa.ontrack.model.structure.Signature.Companion.of
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import java.util.*

class ArtifactoryPromotionSyncServiceImplTest {

    private lateinit var service: ArtifactoryPromotionSyncServiceImpl
    private lateinit var structureService: StructureService
    private lateinit var artifactoryClient: ArtifactoryClient
    private lateinit var project: Project
    private lateinit var branch: Branch
    private lateinit var promotionLevel: PromotionLevel
    private lateinit var propertyService: PropertyService
    private lateinit var build: Build

    @Before
    fun setup() {
        structureService = mock()
        propertyService = mock()
        val artifactoryClientFactory = mock<ArtifactoryClientFactory>()
        val configurationService = mock<ArtifactoryConfigurationService>()
        val artifactoryConfProperties = ArtifactoryConfProperties()
        val securityService = mock<SecurityService>()

        whenever(securityService.asAdmin(any())).thenAnswer { invocation ->
            @Suppress("UNCHECKED_CAST")
            val run = invocation.arguments[0] as () -> Any
            run()
        }

        service = ArtifactoryPromotionSyncServiceImpl(
                structureService,
                propertyService,
                artifactoryClientFactory,
                configurationService,
                artifactoryConfProperties,
                securityService)

        // Fake Artifactory client
        artifactoryClient = mock()
        whenever(artifactoryClientFactory.getClient(ArgumentMatchers.any())).thenReturn(artifactoryClient)

        // Branch to sync
        project = of(NameDescription("P", "Project")).withId(of(1))
        branch = of(
                project,
                NameDescription("B", "Branch")
        ).withId(of(10))

        // Existing build
        build = of(
                branch,
                NameDescription("1.0.0", "Build 1.0.0"),
                of("test")
        ).withId(of(100))
        whenever(structureService.findBuildByName("P", "B", "1.0.0")).thenReturn(
                Optional.of(build)
        )

        // Existing promotions
        whenever(artifactoryClient.getStatuses(ArgumentMatchers.any())).thenReturn(listOf(
                ArtifactoryStatus(
                        "COPPER",
                        "x",
                        Time.now()
                )
        ))

        // Existing promotion level
        promotionLevel = of(
                branch,
                NameDescription("COPPER", "Copper level")
        ).withId(of(100))
        whenever(structureService.findPromotionLevelByName("P", "B", "COPPER")).thenReturn(
                Optional.of(promotionLevel)
        )
    }

    @Test
    fun syncBuild_new_promotion() {

        // Existing promotion run
        whenever(structureService.getLastPromotionRunForBuildAndPromotionLevel(build, promotionLevel)).thenReturn(
                Optional.of(
                        of(
                                build,
                                promotionLevel,
                                of("test"),
                                "Promotion"
                        )
                )
        )

        // Call
        service.syncBuild(branch, "1.0.0", "1.0.0", artifactoryClient) { x: JobRunProgress? -> println(x) }

        // Checks that a promotion has NOT been created
        verify(structureService, times(0)).newPromotionRun(ArgumentMatchers.any())
    }

    @Test
    fun syncBuild_existing_promotion() {

        // No existing promotion run
        whenever(structureService.getLastPromotionRunForBuildAndPromotionLevel(build, promotionLevel)).thenReturn(
                Optional.empty()
        )

        // Call
        service.syncBuild(branch, "1.0.0", "1.0.0", artifactoryClient) { x: JobRunProgress? -> println(x) }

        // Checks that a promotion has been created
        verify(structureService, times(1)).newPromotionRun(ArgumentMatchers.any())
    }

    @Test
    fun syncBuildJobs_one_per_configured_branch() {
        whenever(propertyService.hasProperty(branch, ArtifactoryPromotionSyncPropertyType::class.java)).thenReturn(true)
        val property = Property.of(
                ArtifactoryPromotionSyncPropertyType(
                        ArtifactoryExtensionFeature(),
                        null
                ),
                ArtifactoryPromotionSyncProperty(
                        null,
                        "",
                        "",
                        10
                )
        )
        whenever(propertyService.getProperty(branch, ArtifactoryPromotionSyncPropertyType::class.java)).thenReturn(property)
        whenever(structureService.projectList).thenReturn(listOf(project))
        whenever(structureService.getBranchesForProject(project.id)).thenReturn(listOf(branch))
        // Gets the list of jobs
        Assert.assertEquals(1, service.collectJobRegistrations().count())
    }
}