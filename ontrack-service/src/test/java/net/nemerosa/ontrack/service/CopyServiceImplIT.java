package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.extension.api.support.*;
import net.nemerosa.ontrack.it.AbstractServiceTestSupport;
import net.nemerosa.ontrack.model.security.BranchCreate;
import net.nemerosa.ontrack.model.security.ProjectEdit;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;

import static net.nemerosa.ontrack.model.structure.NameDescription.nd;
import static net.nemerosa.ontrack.model.structure.Replacement.replacementFn;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class CopyServiceImplIT extends AbstractServiceTestSupport {

    @Autowired
    private CopyService service;
    @Autowired
    private StructureService structureService;
    @Autowired
    private PropertyService propertyService;
    @Autowired
    private SecurityService securityService;

    @Test
    public void cloneProject() throws Exception {
        Project sourceProject = doCreateProject();
        Branch sourceBranch = doCreateBranch(sourceProject, nd("B1", "Branch B1"));

        asUser().with(sourceProject, ProjectEdit.class).execute(() -> {
            propertyService.editProperty(
                    sourceProject,
                    TestSimplePropertyType.class,
                    new TestSimpleProperty("http://wiki/P1")
            );
            propertyService.editProperty(
                    sourceBranch,
                    TestSimplePropertyType.class,
                    new TestSimpleProperty("http://wiki/B1")
            );
        });

        // Request
        ProjectCloneRequest request = new ProjectCloneRequest(
                "P2",
                sourceBranch.getId(),
                Arrays.asList(
                        new Replacement("P1", "P2"),
                        new Replacement("B1", "B2")
                )
        );

        // Call
        Project clonedProject = asAdmin()
                .call(() -> service.cloneProject(sourceProject, request));

        // Checks the branch is created
        Branch clonedBranch = structureService.findBranchByName(clonedProject.getName(), "B2").orElseThrow(
                () -> new AssertionError("Cloned branch not found")
        );

        // Checks the copy of properties for the project
        TestSimpleProperty property = asUser().withView(clonedProject).call(() ->
                propertyService.getProperty(
                        clonedProject,
                        TestSimplePropertyType.class
                ).getValue()
        );
        assertNotNull(property);
        assertEquals("http://wiki/P2", property.getValue());

        // Checks the copy of properties for the branch
        property = asUser().withView(clonedProject).call(() ->
                propertyService.getProperty(
                        clonedBranch,
                        TestSimplePropertyType.class
                ).getValue()
        );
        assertNotNull(property);
        assertEquals("http://wiki/B2", property.getValue());
    }

    @Test
    public void cloneBranch() throws Exception {
        Project project = doCreateProject();
        Branch sourceBranch = doCreateBranch(project, nd("B1", "Branch B1"));
        // Request
        BranchCloneRequest request = new BranchCloneRequest(
                "B2",
                Collections.singletonList(
                        new Replacement("B1", "B2")
                )
        );

        // Branch properties
        setProperty(sourceBranch, TestSimplePropertyType.class, new TestSimpleProperty("http://wiki/B1"));

        // Cloning
        Branch clonedBranch = asUser()
                .with(sourceBranch, ProjectEdit.class)
                .call(() ->
                        service.cloneBranch(sourceBranch, request)
                );

        // Checks the branch is created
        assertNotNull(clonedBranch);

        // Checks the copy of properties for the branch
        TestSimpleProperty p = getProperty(clonedBranch, TestSimplePropertyType.class);
        assertNotNull(p);
        assertEquals("http://wiki/B2", p.getValue());
    }

    @Test
    @Ignore
    public void bulkUpdateBranch() {
        Project project = Project.of(nd("P1", "")).withId(ID.of(1));
        Branch branch = Branch.of(project, nd("B1", "Branch B1")).withId(ID.of(1));
        // Request
        BranchBulkUpdateRequest request = new BranchBulkUpdateRequest(
                Collections.singletonList(
                        new Replacement("B1", "B2")
                )
        );

        // Branch properties
        TestPropertyType testPropertyType = new TestPropertyType(
                new TestExtensionFeature()
        );
        when(propertyService.getProperties(branch)).thenReturn(
                Collections.singletonList(
                        Property.of(
                                testPropertyType,
                                TestProperty.of("http://wiki/B1")
                        )
                )
        );

        // Updated branch
        Branch updatedBranch = branch.withDescription("Branch B2");

        // Edition of the property must be allowed
        when(securityService.isProjectFunctionGranted(updatedBranch, ProjectEdit.class)).thenReturn(true);

        // Updating
        service.update(branch, request);

        // Checks the branch is updated
        verify(structureService, times(1)).saveBranch(updatedBranch);

        // Checks the copy of properties for the branch
        verify(propertyService, times(1)).copyProperty(
                eq(branch),
                eq(Property.of(testPropertyType, TestProperty.of("http://wiki/B1"))),
                eq(updatedBranch),
                any()
        );
    }

    @Test
    @Ignore
    public void doCopyBranchProperties() {
        Branch sourceBranch = Branch.of(Project.of(nd("P1", "")).withId(ID.of(1)), nd("B1", "")).withId(ID.of(1));
        Branch targetBranch = Branch.of(Project.of(nd("P2", "")).withId(ID.of(2)), nd("B2", "")).withId(ID.of(2));
        // Request
        Function<String, String> replacementFn = replacementFn(
                Collections.singletonList(
                        new Replacement("P1", "P2")
                )
        );

        // Properties for the branch
        TestPropertyType testPropertyType = new TestPropertyType(
                new TestExtensionFeature()
        );
        when(propertyService.getProperties(sourceBranch)).thenReturn(
                Collections.singletonList(
                        Property.of(
                                testPropertyType,
                                TestProperty.of("http://wiki/P1")
                        )
                )
        );

        // Edition of the property must be allowed
        when(securityService.isProjectFunctionGranted(targetBranch, ProjectEdit.class)).thenReturn(true);

        // Copy
        // FIXME service.doCopy(sourceBranch, targetBranch, replacementFn, SyncPolicy.COPY);

        // Checks the copy of properties for the branch
        verify(propertyService, times(1)).copyProperty(
                eq(sourceBranch),
                eq(Property.of(testPropertyType, TestProperty.of("http://wiki/P1"))),
                eq(targetBranch),
                any()
        );
    }

    @Test
    @Ignore
    public void doCopyPromotionLevels() {
        Branch sourceBranch = Branch.of(Project.of(nd("P1", "")).withId(ID.of(1)), nd("B1", "")).withId(ID.of(1));
        Branch targetBranch = Branch.of(Project.of(nd("P2", "")).withId(ID.of(2)), nd("B2", "")).withId(ID.of(2));
        // Request
        Function<String, String> replacementFn = replacementFn(
                Collections.singletonList(
                        new Replacement("P1", "P2")
                )
        );
        // Promotion levels for source
        PromotionLevel sourcePromotionLevel = PromotionLevel.of(sourceBranch, nd("copper", "Copper level for P1"));
        when(structureService.getPromotionLevelListForBranch(ID.of(1))).thenReturn(
                Collections.singletonList(
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
        TestPropertyType testPropertyType = new TestPropertyType(
                new TestExtensionFeature()
        );
        when(propertyService.getProperties(sourcePromotionLevel)).thenReturn(
                Collections.singletonList(
                        Property.of(
                                testPropertyType,
                                TestProperty.of("http://wiki/P1")
                        )
                )
        );

        // Edition of the property must be allowed
        when(securityService.isProjectFunctionGranted(targetPromotionLevel, ProjectEdit.class)).thenReturn(true);

        // Copy
        // FIXME service.doCopyPromotionLevels(sourceBranch, targetBranch, replacementFn, SyncPolicy.COPY);

        // Checks the promotion level was created
        verify(structureService, times(1)).newPromotionLevel(targetPromotionLevel);
        // Checks the copy of properties for the promotion levels
        verify(propertyService, times(1)).copyProperty(
                eq(sourcePromotionLevel),
                eq(Property.of(testPropertyType, TestProperty.of("http://wiki/P1"))),
                eq(targetPromotionLevel),
                any()
        );
    }

    @Test
    @Ignore
    public void doCopyValidationStamps() {
        Branch sourceBranch = Branch.of(Project.of(nd("P1", "")).withId(ID.of(1)), nd("B1", "")).withId(ID.of(1));
        Branch targetBranch = Branch.of(Project.of(nd("P2", "")).withId(ID.of(2)), nd("B2", "")).withId(ID.of(2));
        // Request
        Function<String, String> replacementFn = replacementFn(
                Collections.singletonList(
                        new Replacement("P1", "P2")
                )
        );
        // Validation stamps for source
        Signature signature = Signature.of(LocalDateTime.of(2016, 12, 27, 19, 24), "test");
        ValidationStamp sourceValidationStamp = ValidationStamp.of(sourceBranch, nd("smoke", "Smoke test for P1"))
                .withSignature(signature);
        when(structureService.getValidationStampListForBranch(ID.of(1))).thenReturn(
                Collections.singletonList(
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
        when(structureService.newValidationStamp(targetValidationStamp)).thenReturn(targetValidationStamp.withSignature(signature));

        // Properties for the validation stamp
        TestPropertyType testPropertyType = new TestPropertyType(
                new TestExtensionFeature()
        );
        when(propertyService.getProperties(sourceValidationStamp)).thenReturn(
                Collections.singletonList(
                        Property.of(
                                testPropertyType,
                                TestProperty.of("http://wiki/P1")
                        )
                )
        );

        // Edition of the property must be allowed
        when(securityService.isProjectFunctionGranted(targetValidationStamp, ProjectEdit.class)).thenReturn(true);

        // Copy
        // FIXME service.doCopyValidationStamps(sourceBranch, targetBranch, replacementFn, SyncPolicy.COPY);

        // Checks the validation stamp was created
        verify(structureService, times(1)).newValidationStamp(targetValidationStamp.withSignature(signature));
        // Checks the copy of properties for the validation stamps
        verify(propertyService, times(1)).copyProperty(
                eq(sourceValidationStamp),
                eq(Property.of(testPropertyType, TestProperty.of("http://wiki/P1"))),
                eq(targetValidationStamp),
                any()
        );
    }
}