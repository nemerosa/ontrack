package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static net.nemerosa.ontrack.model.structure.NameDescription.nd;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class CopyServiceImplTest {

    private CopyServiceImpl service;
    private StructureService structureService;

    @Before
    public void before() {
        structureService = mock(StructureService.class);
        SecurityService securityService = mock(SecurityService.class);
        service = new CopyServiceImpl(structureService, securityService);
    }

    @Test
    public void applyReplacements_none() {
        assertEquals("branches/11.7", CopyServiceImpl.applyReplacements("branches/11.7", Collections.emptyList()));
    }

    @Test
    public void applyReplacements_direct() {
        assertEquals("branches/11.8", CopyServiceImpl.applyReplacements("branches/11.7", Arrays.asList(
                new Replacement("11.7", "11.8")
        )));
    }

    @Test
    public void applyReplacements_several() {
        assertEquals("Release pipeline for branches/11.7", CopyServiceImpl.applyReplacements("Pipeline for trunk", Arrays.asList(
                new Replacement("trunk", "branches/11.7"),
                new Replacement("Pipeline", "Release pipeline")
        )));
    }

    @Test
    public void doCopyPromotionLevels() {
        Branch sourceBranch = Branch.of(Project.of(nd("P1", "")).withId(ID.of(1)), nd("B1", "")).withId(ID.of(1));
        Branch targetBranch = Branch.of(Project.of(nd("P2", "")).withId(ID.of(2)), nd("B2", "")).withId(ID.of(2));
        // Request
        BranchCopyRequest request = new BranchCopyRequest(
                ID.of(1),
                Collections.emptyList(),
                Arrays.asList(
                        new Replacement("P1", "P2")
                ),
                Collections.emptyList()
        );
        // Promotion levels for source
        when(structureService.getPromotionLevelListForBranch(ID.of(1))).thenReturn(
                Arrays.asList(
                        PromotionLevel.of(sourceBranch, nd("copper", "Copper level for P1"))
                )
        );
        when(structureService.findPromotionLevelByName("P2", "B2", "copper")).thenReturn(Optional.empty());
        // Copy
        service.doCopyPromotionLevels(sourceBranch, targetBranch, request);
        // Checks that the promotion level is created for the target branch
        verify(structureService, times(1)).newPromotionLevel(
                PromotionLevel.of(
                        targetBranch,
                        nd("copper", "Copper level for P2")
                )
        );
    }
}