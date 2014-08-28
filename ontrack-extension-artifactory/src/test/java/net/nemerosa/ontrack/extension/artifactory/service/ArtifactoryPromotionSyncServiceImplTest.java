package net.nemerosa.ontrack.extension.artifactory.service;

import net.nemerosa.ontrack.extension.artifactory.client.ArtifactoryClient;
import net.nemerosa.ontrack.extension.artifactory.client.ArtifactoryClientFactory;
import net.nemerosa.ontrack.extension.artifactory.model.ArtifactoryStatus;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.model.support.Time;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ArtifactoryPromotionSyncServiceImplTest {

    private ArtifactoryPromotionSyncServiceImpl service;
    private StructureService structureService;
    private ArtifactoryClient artifactoryClient;
    private Branch branch;
    private PromotionLevel promotionLevel;
    private Build build;

    @Before
    public void setup() {
        structureService = mock(StructureService.class);
        PropertyService propertyService = mock(PropertyService.class);
        ArtifactoryClientFactory artifactoryClientFactory = mock(ArtifactoryClientFactory.class);
        service = new ArtifactoryPromotionSyncServiceImpl(
                structureService,
                propertyService,
                artifactoryClientFactory
        );

        // Fake Artifactory client
        artifactoryClient = mock(ArtifactoryClient.class);
        when(artifactoryClientFactory.getClient(any())).thenReturn(artifactoryClient);

        // Branch to sync
        branch = Branch.of(
                Project.of(new NameDescription("P", "Project")).withId(ID.of(1)),
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
        when(artifactoryClient.getStatuses(any())).thenReturn(Arrays.asList(
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

}
