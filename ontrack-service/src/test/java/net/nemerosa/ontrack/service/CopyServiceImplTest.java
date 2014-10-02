package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.buildfilter.BuildFilterService;
import net.nemerosa.ontrack.model.security.ProjectEdit;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.service.support.property.TestProperty;
import net.nemerosa.ontrack.service.support.property.TestPropertyType;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static net.nemerosa.ontrack.json.JsonUtils.object;
import static net.nemerosa.ontrack.model.structure.NameDescription.nd;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class CopyServiceImplTest {

    private CopyServiceImpl service;
    private StructureService structureService;
    private PropertyService propertyService;
    private SecurityService securityService;

    @Before
    public void before() {
        structureService = mock(StructureService.class);
        propertyService = mock(PropertyService.class);
        securityService = mock(SecurityService.class);
        BuildFilterService buildFilterService = mock(BuildFilterService.class);
        service = new CopyServiceImpl(structureService, propertyService, securityService, buildFilterService);
    }

    @Test
    public void applyReplacements_none() {
        assertEquals("branches/11.7", CopyServiceImpl.applyReplacements("branches/11.7", Collections.emptyList()));
    }

    @Test
    public void applyReplacements_null() {
        assertEquals("branches/11.7", CopyServiceImpl.applyReplacements("branches/11.7", Arrays.asList(
                new Replacement(null, "any")
        )));
    }

    @Test
    public void applyReplacements_blank() {
        assertEquals("branches/11.7", CopyServiceImpl.applyReplacements("branches/11.7", Arrays.asList(
                new Replacement("", "any")
        )));
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
    public void cloneProject() {
        Project sourceProject = Project.of(nd("P1", "Project P1")).withId(ID.of(1));
        Branch sourceBranch = Branch.of(sourceProject, nd("B1", "Branch B1")).withId(ID.of(1));

        // Request
        ProjectCloneRequest request = new ProjectCloneRequest(
                "P2",
                sourceBranch.getId(),
                Arrays.asList(
                        new Replacement("P1", "P2"),
                        new Replacement("B1", "B2")
                )
        );

        // Branch properties
        when(propertyService.getProperties(sourceBranch)).thenReturn(
                Arrays.asList(
                        Property.of(
                                new TestPropertyType(),
                                TestProperty.of("http://wiki/B1")
                        )
                )
        );

        // Project properties
        when(propertyService.getProperties(sourceProject)).thenReturn(
                Arrays.asList(
                        Property.of(
                                new TestPropertyType(),
                                TestProperty.of("http://wiki/P1")
                        )
                )
        );

        // Access to the source branch
        when(structureService.getBranch(ID.of(1))).thenReturn(sourceBranch);

        // Created branch and project
        Project projectToCreate = Project.of(nd("P2", "Project P2"));
        Project createdProject = projectToCreate.withId(ID.of(2));
        Branch branchToCreate = Branch.of(createdProject, nd("B2", "Branch B2"));
        Branch createdBranch = branchToCreate.withId(ID.of(2));

        // Creation of the project and the branch
        when(structureService.newProject(projectToCreate)).thenReturn(createdProject);
        when(structureService.newBranch(branchToCreate)).thenReturn(createdBranch);

        // Edition of the property must be allowed
        when(securityService.isProjectFunctionGranted(createdProject, ProjectEdit.class)).thenReturn(true);
        when(securityService.isProjectFunctionGranted(createdBranch, ProjectEdit.class)).thenReturn(true);

        // Call
        service.cloneProject(sourceProject, request);

        // Checks the branch is created
        verify(structureService, times(1)).newProject(projectToCreate);
        verify(structureService, times(1)).newBranch(branchToCreate);

        // Checks the copy of properties for the project
        verify(propertyService, times(1)).editProperty(
                eq(createdProject),
                eq(TestPropertyType.class.getName()),
                eq(object()
                        .with("value", "http://wiki/P2")
                        .end())
        );

        // Checks the copy of properties for the branch
        verify(propertyService, times(1)).editProperty(
                eq(createdBranch),
                eq(TestPropertyType.class.getName()),
                eq(object()
                        .with("value", "http://wiki/B2")
                        .end())
        );
    }

    @Test
    public void cloneBranch() {
        Project project = Project.of(nd("P1", "")).withId(ID.of(1));
        Branch sourceBranch = Branch.of(project, nd("B1", "Branch B1")).withId(ID.of(1));
        // Request
        BranchCloneRequest request = new BranchCloneRequest(
                "B2",
                Arrays.asList(
                        new Replacement("B1", "B2")
                )
        );

        // Branch properties
        when(propertyService.getProperties(sourceBranch)).thenReturn(
                Arrays.asList(
                        Property.of(
                                new TestPropertyType(),
                                TestProperty.of("http://wiki/B1")
                        )
                )
        );

        // Created branch
        Branch targetBranch = Branch.of(project, nd("B2", "Branch B2"));

        // Creation of the branch
        when(structureService.newBranch(targetBranch)).thenReturn(targetBranch.withId(ID.of(2)));

        // Edition of the property must be allowed
        when(securityService.isProjectFunctionGranted(targetBranch.withId(ID.of(2)), ProjectEdit.class)).thenReturn(true);

        // Cloning
        service.cloneBranch(sourceBranch, request);

        // Checks the branch is created
        verify(structureService, times(1)).newBranch(targetBranch);

        // Checks the copy of properties for the branch
        verify(propertyService, times(1)).editProperty(
                eq(targetBranch.withId(ID.of(2))),
                eq(TestPropertyType.class.getName()),
                eq(object()
                        .with("value", "http://wiki/B2")
                        .end())
        );
    }

    @Test
    public void doCopyBranchProperties() {
        Branch sourceBranch = Branch.of(Project.of(nd("P1", "")).withId(ID.of(1)), nd("B1", "")).withId(ID.of(1));
        Branch targetBranch = Branch.of(Project.of(nd("P2", "")).withId(ID.of(2)), nd("B2", "")).withId(ID.of(2));
        // Request
        BranchCopyRequest request = new BranchCopyRequest(
                ID.of(1),
                Arrays.asList(
                        new Replacement("P1", "P2")
                )
        );

        // Properties for the branch
        when(propertyService.getProperties(sourceBranch)).thenReturn(
                Arrays.asList(
                        Property.of(
                                new TestPropertyType(),
                                TestProperty.of("http://wiki/P1")
                        )
                )
        );

        // Edition of the property must be allowed
        when(securityService.isProjectFunctionGranted(targetBranch, ProjectEdit.class)).thenReturn(true);

        // Copy
        service.doCopy(sourceBranch, targetBranch, request);

        // Checks the copy of properties for the branch
        verify(propertyService, times(1)).editProperty(
                eq(targetBranch),
                eq(TestPropertyType.class.getName()),
                eq(object()
                        .with("value", "http://wiki/P2")
                        .end())
        );
    }

    @Test
    public void doCopyPromotionLevels() {
        Branch sourceBranch = Branch.of(Project.of(nd("P1", "")).withId(ID.of(1)), nd("B1", "")).withId(ID.of(1));
        Branch targetBranch = Branch.of(Project.of(nd("P2", "")).withId(ID.of(2)), nd("B2", "")).withId(ID.of(2));
        // Request
        BranchCopyRequest request = new BranchCopyRequest(
                ID.of(1),
                Arrays.asList(
                        new Replacement("P1", "P2")
                )
        );
        // Promotion levels for source
        PromotionLevel sourcePromotionLevel = PromotionLevel.of(sourceBranch, nd("copper", "Copper level for P1"));
        when(structureService.getPromotionLevelListForBranch(ID.of(1))).thenReturn(
                Arrays.asList(
                        sourcePromotionLevel
                )
        );
        when(structureService.findPromotionLevelByName("P2", "B2", "copper")).thenReturn(Optional.empty());

        // Promotion level supposed to be created for the target branch
        PromotionLevel targetPromotionLevel = PromotionLevel.of(
                targetBranch,
                nd("copper", "Copper level for P2")
        );
        // Result of the creation
        when(structureService.newPromotionLevel(targetPromotionLevel)).thenReturn(targetPromotionLevel);

        // Properties for the promotion level
        when(propertyService.getProperties(sourcePromotionLevel)).thenReturn(
                Arrays.asList(
                        Property.of(
                                new TestPropertyType(),
                                TestProperty.of("http://wiki/P1")
                        )
                )
        );

        // Edition of the property must be allowed
        when(securityService.isProjectFunctionGranted(targetPromotionLevel, ProjectEdit.class)).thenReturn(true);

        // Copy
        service.doCopyPromotionLevels(sourceBranch, targetBranch, request);

        // Checks the promotion level was created
        verify(structureService, times(1)).newPromotionLevel(targetPromotionLevel);
        // Checks the copy of properties for the promotion levels
        verify(propertyService, times(1)).editProperty(
                eq(targetPromotionLevel),
                eq(TestPropertyType.class.getName()),
                eq(object()
                        .with("value", "http://wiki/P2")
                        .end())
        );
    }

    @Test
    public void doCopyValidationStamps() {
        Branch sourceBranch = Branch.of(Project.of(nd("P1", "")).withId(ID.of(1)), nd("B1", "")).withId(ID.of(1));
        Branch targetBranch = Branch.of(Project.of(nd("P2", "")).withId(ID.of(2)), nd("B2", "")).withId(ID.of(2));
        // Request
        BranchCopyRequest request = new BranchCopyRequest(
                ID.of(1),
                Arrays.asList(
                        new Replacement("P1", "P2")
                )
        );
        // Validation stamps for source
        ValidationStamp sourceValidationStamp = ValidationStamp.of(sourceBranch, nd("smoke", "Smoke test for P1"));
        when(structureService.getValidationStampListForBranch(ID.of(1))).thenReturn(
                Arrays.asList(
                        sourceValidationStamp
                )
        );
        when(structureService.findValidationStampByName("P2", "B2", "smoke")).thenReturn(Optional.empty());

        // Validation stamp supposed to be created for the target branch
        ValidationStamp targetValidationStamp = ValidationStamp.of(
                targetBranch,
                nd("smoke", "Smoke test for P2")
        );
        // Result of the creation
        when(structureService.newValidationStamp(targetValidationStamp)).thenReturn(targetValidationStamp);

        // Properties for the validation stamp
        when(propertyService.getProperties(sourceValidationStamp)).thenReturn(
                Arrays.asList(
                        Property.of(
                                new TestPropertyType(),
                                TestProperty.of("http://wiki/P1")
                        )
                )
        );

        // Edition of the property must be allowed
        when(securityService.isProjectFunctionGranted(targetValidationStamp, ProjectEdit.class)).thenReturn(true);

        // Copy
        service.doCopyValidationStamps(sourceBranch, targetBranch, request);

        // Checks the validation stamp was created
        verify(structureService, times(1)).newValidationStamp(targetValidationStamp);
        // Checks the copy of properties for the validation stamps
        verify(propertyService, times(1)).editProperty(
                eq(targetValidationStamp),
                eq(TestPropertyType.class.getName()),
                eq(object()
                        .with("value", "http://wiki/P2")
                        .end())
        );
    }
}