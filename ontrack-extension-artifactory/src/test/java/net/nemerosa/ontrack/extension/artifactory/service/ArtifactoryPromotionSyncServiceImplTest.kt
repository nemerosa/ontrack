package net.nemerosa.ontrack.extension.artifactory.service;

import net.nemerosa.ontrack.common.Time;
import net.nemerosa.ontrack.extension.artifactory.ArtifactoryConfProperties;
import net.nemerosa.ontrack.extension.artifactory.ArtifactoryExtensionFeature;
import net.nemerosa.ontrack.extension.artifactory.client.ArtifactoryClient;
import net.nemerosa.ontrack.extension.artifactory.client.ArtifactoryClientFactory;
import net.nemerosa.ontrack.extension.artifactory.configuration.ArtifactoryConfigurationService;
import net.nemerosa.ontrack.extension.artifactory.model.ArtifactoryStatus;
import net.nemerosa.ontrack.extension.artifactory.property.ArtifactoryPromotionSyncProperty;
import net.nemerosa.ontrack.extension.artifactory.property.ArtifactoryPromotionSyncPropertyType;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ArtifactoryPromotionSyncServiceImplTest {

    private ArtifactoryPromotionSyncServiceImpl service;
    private StructureService structureService;
    private ArtifactoryClient artifactoryClient;
    private Project project;
    private Branch branch;
    private PromotionLevel promotionLevel;
    PropertyService propertyService;
    private Build build;

    @Before
    public void setup() {
        structureService = mock(StructureService.class);
        propertyService = mock(PropertyService.class);
        ArtifactoryClientFactory artifactoryClientFactory = mock(ArtifactoryClientFactory.class);
        ArtifactoryConfigurationService configurationService = mock(ArtifactoryConfigurationService.class);
        ArtifactoryConfProperties artifactoryConfProperties = new ArtifactoryConfProperties();

        SecurityService securityService = mock(SecurityService.class);
        doAnswer(invocation -> {
            Supplier run = (Supplier) invocation.getArguments()[0];
            return run.get();
        }).when(securityService).asAdmin(any(Supplier.class));

        service = new ArtifactoryPromotionSyncServiceImpl(
                structureService,
                propertyService,
                artifactoryClientFactory,
                configurationService,
                artifactoryConfProperties,
                securityService);

        // Fake Artifactory client
        artifactoryClient = mock(ArtifactoryClient.class);
        when(artifactoryClientFactory.getClient(any())).thenReturn(artifactoryClient);

        // Branch to sync
        project = Project.of(new NameDescription("P", "Project")).withId(ID.of(1));
        branch = Branch.of(
                project,
                new NameDescription("B", "Branch")
        ).withId(ID.of(10));

        // Existing build
        build = Build.of(
                branch,
                new NameDescription("1.0.0", "Build 1.0.0"),
                Signature.of("test")
        ).withId(ID.of(100));
        when(structureService.findBuildByName("P", "B", "1.0.0")).thenReturn(
                Optional.of(build)
        );

        // Existing promotions
        when(artifactoryClient.getStatuses(any())).thenReturn(Collections.singletonList(
                new ArtifactoryStatus(
                        "COPPER",
                        "x",
                        Time.now()
                )
        ));

        // Existing promotion level
        promotionLevel = PromotionLevel.of(
                branch,
                new NameDescription("COPPER", "Copper level")
        ).withId(ID.of(100));
        when(structureService.findPromotionLevelByName("P", "B", "COPPER")).thenReturn(
                Optional.of(promotionLevel)
        );

    }

    @Test
    public void syncBuild_new_promotion() {

        // Existing promotion run
        when(structureService.getLastPromotionRunForBuildAndPromotionLevel(build, promotionLevel)).thenReturn(
                Optional.of(
                        PromotionRun.of(
                                build,
                                promotionLevel,
                                Signature.of("test"),
                                "Promotion"
                        )
                )
        );

        // Call
        service.syncBuild(branch, "1.0.0", "1.0.0", artifactoryClient, System.out::println);

        // Checks that a promotion has NOT been created
        verify(structureService, times(0)).newPromotionRun(any());

    }

    @Test
    public void syncBuild_existing_promotion() {

        // No existing promotion run
        when(structureService.getLastPromotionRunForBuildAndPromotionLevel(build, promotionLevel)).thenReturn(
                Optional.empty()
        );

        // Call
        service.syncBuild(branch, "1.0.0", "1.0.0", artifactoryClient, System.out::println);

        // Checks that a promotion has been created
        verify(structureService, times(1)).newPromotionRun(any());

    }

    @Test
    public void syncBuildJobs_one_per_configured_branch() {
        when(propertyService.hasProperty(branch, ArtifactoryPromotionSyncPropertyType.class)).thenReturn(true);
        Property<ArtifactoryPromotionSyncProperty> property = Property.of(
                new ArtifactoryPromotionSyncPropertyType(
                        new ArtifactoryExtensionFeature(),
                        null
                ),
                new ArtifactoryPromotionSyncProperty(
                        null,
                        "",
                        "",
                        10
                )
        );
        when(propertyService.getProperty(branch, ArtifactoryPromotionSyncPropertyType.class)).thenReturn(property);
        when(structureService.getProjectList()).thenReturn(Collections.singletonList(project));
        when(structureService.getBranchesForProject(project.getId())).thenReturn(Collections.singletonList(branch));
        // Gets the list of jobs
        assertEquals(1, service.collectJobRegistrations().count());
    }

}
