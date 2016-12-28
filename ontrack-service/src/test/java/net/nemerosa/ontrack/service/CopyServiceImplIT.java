package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.extension.api.support.TestSimpleProperty;
import net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType;
import net.nemerosa.ontrack.it.AbstractServiceTestSupport;
import net.nemerosa.ontrack.model.security.ProjectEdit;
import net.nemerosa.ontrack.model.structure.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;

import static net.nemerosa.ontrack.model.structure.NameDescription.nd;
import static net.nemerosa.ontrack.model.structure.Replacement.replacementFn;
import static net.nemerosa.ontrack.test.TestUtils.uid;
import static org.junit.Assert.*;

public class CopyServiceImplIT extends AbstractServiceTestSupport {

    @Autowired
    private CopyService service;
    @Autowired
    private StructureService structureService;
    @Autowired
    private PropertyService propertyService;

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
        String targetProjectName = uid("P");
        ProjectCloneRequest request = new ProjectCloneRequest(
                targetProjectName,
                sourceBranch.getId(),
                Arrays.asList(
                        new Replacement("P1", "P2"),
                        new Replacement("B1", "B2")
                )
        );

        // Call
        Project clonedProject = asAdmin()
                .call(() -> service.cloneProject(sourceProject, request));

        assertEquals(targetProjectName, clonedProject.getName());

        // Checks the branch is created
        Branch clonedBranch = asUserWithView(clonedProject).call(() ->
                structureService.findBranchByName(clonedProject.getName(), "B2")
                        .orElseThrow(
                                () -> new AssertionError("Cloned branch not found")
                        )
        );

        // Checks the copy of properties for the project
        TestSimpleProperty property = asUserWithView(clonedProject).call(() ->
                propertyService.getProperty(
                        clonedProject,
                        TestSimplePropertyType.class
                ).getValue()
        );
        assertNotNull(property);
        assertEquals("http://wiki/P2", property.getValue());

        // Checks the copy of properties for the branch
        property = asUserWithView(clonedProject).call(() ->
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
    public void bulkUpdateBranch() throws Exception {
        Branch branch = doCreateBranch();
        // Request
        BranchBulkUpdateRequest request = new BranchBulkUpdateRequest(
                Collections.singletonList(
                        new Replacement("B1", "B2")
                )
        );

        // Branch properties
        setProperty(
                branch,
                TestSimplePropertyType.class,
                new TestSimpleProperty("http://wiki/B1")
        );

        // Updating
        Branch updatedBranch = asUser().with(branch, ProjectEdit.class).call(() -> service.update(branch, request));

        // Checks the copy of properties for the branch
        TestSimpleProperty p = getProperty(updatedBranch, TestSimplePropertyType.class);
        assertNotNull(p);
        assertEquals("http://wiki/B2", p.getValue());
    }

    @Test
    public void doCopyBranchProperties() throws Exception {
        Branch sourceBranch = doCreateBranch();
        Branch targetBranch = doCreateBranch();
        // Request
        Function<String, String> replacementFn = replacementFn(
                Collections.singletonList(
                        new Replacement("B1", "B2")
                )
        );

        // Properties for the branch
        setProperty(sourceBranch, TestSimplePropertyType.class, new TestSimpleProperty("http://wiki/B1"));

        // Copy
        asUser()
                .withView(sourceBranch)
                .with(targetBranch, ProjectEdit.class).execute(() ->
                service.copy(targetBranch, sourceBranch, replacementFn, SyncPolicy.COPY)
        );

        // Checks the copy of properties for the branch
        TestSimpleProperty p = getProperty(targetBranch, TestSimplePropertyType.class);
        assertNotNull(p);
        assertEquals("http://wiki/B2", p.getValue());
    }

    @Test
    public void doCopyPromotionLevels() throws Exception {
        Branch sourceBranch = doCreateBranch();
        Branch targetBranch = doCreateBranch();
        // Request
        Function<String, String> replacementFn = replacementFn(
                Collections.singletonList(
                        new Replacement("P1", "P2")
                )
        );
        // Promotion levels for source
        PromotionLevel sourcePromotionLevel = doCreatePromotionLevel(sourceBranch, nd("copper", "Copper level for P1"));

        // Properties for the promotion level
        setProperty(sourcePromotionLevel, TestSimplePropertyType.class, new TestSimpleProperty("http://wiki/P1"));

        // Copy
        asUser()
                .withView(sourceBranch)
                .with(targetBranch, ProjectEdit.class).execute(() ->
                service.copy(targetBranch, sourceBranch, replacementFn, SyncPolicy.COPY)
        );

        // Checks the promotion level was created
        Optional<PromotionLevel> oPL = asUserWithView(targetBranch).call(() -> structureService.findPromotionLevelByName(
                targetBranch.getProject().getName(),
                targetBranch.getName(),
                "copper"
        ));
        assertTrue(oPL.isPresent());
        // Checks the copy of properties for the promotion levels
        TestSimpleProperty p = getProperty(oPL.orElse(null), TestSimplePropertyType.class);
        assertNotNull(p);
        assertEquals("http://wiki/P2", p.getValue());
    }

    @Test
    public void doCopyValidationStamps() throws Exception {
        Branch sourceBranch = doCreateBranch();
        Branch targetBranch = doCreateBranch();
        // Request
        Function<String, String> replacementFn = replacementFn(
                Collections.singletonList(
                        new Replacement("P1", "P2")
                )
        );
        // Validation stamps for source
        ValidationStamp sourceValidationStamp = doCreateValidationStamp(sourceBranch, nd("smoke", "Smoke test for P1"));

        // Properties for the validation stamp
        setProperty(
                sourceValidationStamp,
                TestSimplePropertyType.class,
                new TestSimpleProperty("http://wiki/P1")
        );

        // Copy
        asUser()
                .withView(sourceBranch)
                .with(targetBranch, ProjectEdit.class).execute(() ->
                service.copy(targetBranch, sourceBranch, replacementFn, SyncPolicy.COPY)
        );

        // Checks the validation stamp was created
        Optional<ValidationStamp> oVS = asUserWithView(targetBranch).call(() -> structureService.findValidationStampByName(
                targetBranch.getProject().getName(),
                targetBranch.getName(),
                "smoke"
        ));
        assertTrue(oVS.isPresent());
        // Checks the copy of properties for the validation stamps
        TestSimpleProperty p = getProperty(oVS.orElse(null), TestSimplePropertyType.class);
        assertNotNull(p);
        assertEquals("http://wiki/P2", p.getValue());
    }
}