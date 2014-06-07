package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.it.AbstractITTestSupport;
import net.nemerosa.ontrack.model.exceptions.ImageFileSizeException;
import net.nemerosa.ontrack.model.exceptions.ImageTypeNotAcceptedException;
import net.nemerosa.ontrack.model.security.*;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.test.TestUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

import static org.junit.Assert.*;

public class StructureServiceTest extends AbstractITTestSupport {

    @Autowired
    private StructureService structureService;

    @Autowired
    private ValidationRunStatusService validationRunStatusService;

    @Test(expected = IllegalArgumentException.class)
    public void newProject_null() throws Exception {
        structureService.newProject(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void newProject_existing() throws Exception {
        structureService.newProject(Project.of(nameDescription()).withId(ID.of(1)));
    }

    @Test
    public void newProject() throws Exception {
        Project project = doCreateProject();
        assertNotNull(project);
        Entity.isEntityDefined(project, "Project must be defined");
        Project p = asUser().with(project.id(), ProjectView.class).call(() -> structureService.getProject(project.getId()));
        assertEquals(project, p);
    }

    @Test
    public void newBranch() throws Exception {
        Branch branch = doCreateBranch();
        assertNotNull(branch);
        Branch b = asUser().with(branch.getProject().id(), ProjectView.class).call(() -> structureService.getBranch(branch.getId()));
        assertEquals(branch, b);
    }

    @Test
    public void getProjectList_all() throws Exception {
        int[] ids = doCreateProjects();
        List<Project> list = asUser().with(ProjectList.class).call(structureService::getProjectList);
        assertTrue(list.size() >= 3);
        assertTrue(list.stream().filter(p -> p.id() == ids[0]).findFirst().isPresent());
        assertTrue(list.stream().filter(p -> p.id() == ids[1]).findFirst().isPresent());
        assertTrue(list.stream().filter(p -> p.id() == ids[2]).findFirst().isPresent());
    }

    @Test
    public void getProjectList_filtered() throws Exception {
        int[] ids = doCreateProjects();
        List<Project> list = asUser()
                .with(ids[0], ProjectView.class)
                .with(ids[1], ProjectEdit.class)
                .call(structureService::getProjectList);
        assertEquals(2, list.size());
        assertTrue(list.stream().filter(p -> p.id() == ids[0]).findFirst().isPresent());
        assertTrue(list.stream().filter(p -> p.id() == ids[1]).findFirst().isPresent());
        assertTrue(!list.stream().filter(p -> p.id() == ids[2]).findFirst().isPresent());
    }

    @Test
    public void getProjectList_none() throws Exception {
        doCreateProjects();
        List<Project> list = asUser()
                .call(structureService::getProjectList);
        assertEquals(0, list.size());
    }

    @Test
    public void promotionLevel_image_none() throws Exception {
        PromotionLevel promotionLevel = doCreatePromotionLevel();
        Document image = view(promotionLevel, () -> structureService.getPromotionLevelImage(promotionLevel.getId()));
        assertNull("No image", image);
    }

    @Test
    public void promotionLevel_image_add() throws Exception {
        PromotionLevel promotionLevel = doCreatePromotionLevel();
        // Gets an image
        Document image = new Document("image/png", TestUtils.resourceBytes("/promotionLevelImage1.png"));
        // Sets the image
        asUser().with(promotionLevel.getBranch().getProject().id(), PromotionLevelEdit.class).call(() -> {
            structureService.setPromotionLevelImage(promotionLevel.getId(), image);
            return null;
        });
        // Gets the image
        Document d = view(promotionLevel, () -> structureService.getPromotionLevelImage(promotionLevel.getId()));
        // Checks
        assertEquals(image, d);
    }

    @Test(expected = ImageTypeNotAcceptedException.class)
    public void promotionLevel_image_type_not_acceptable() throws Exception {
        PromotionLevel promotionLevel = doCreatePromotionLevel();
        // Gets an image
        Document image = new Document("image/x", new byte[1]);
        // Sets the image
        structureService.setPromotionLevelImage(promotionLevel.getId(), image);
    }

    @Test(expected = ImageFileSizeException.class)
    public void promotionLevel_image_size_not_acceptable() throws Exception {
        PromotionLevel promotionLevel = doCreatePromotionLevel();
        // Gets an image
        Document image = new Document("image/png", new byte[16001]);
        // Sets the image
        structureService.setPromotionLevelImage(promotionLevel.getId(), image);
    }

    @Test
    public void promotionLevel_image_null() throws Exception {
        PromotionLevel promotionLevel = doCreatePromotionLevel();
        // Gets an image
        Document image = new Document("image/png", TestUtils.resourceBytes("/promotionLevelImage1.png"));
        // Sets the image
        asUser().with(promotionLevel.getBranch().getProject().id(), PromotionLevelEdit.class).call(() -> {
            // New image
            structureService.setPromotionLevelImage(promotionLevel.getId(), image);
            // Removes the image
            structureService.setPromotionLevelImage(promotionLevel.getId(), null);
            return null;
        });
        // Gets the image
        Document d = view(promotionLevel, () -> structureService.getPromotionLevelImage(promotionLevel.getId()));
        // Checks
        assertNull(d);
    }

    @Test(expected = AccessDeniedException.class)
    public void validationRun_create_grant_check() throws Exception {
        // Prerequisites
        Branch branch = doCreateBranch();
        ValidationStamp stamp = doCreateValidationStamp(branch, nameDescription());
        Build build = doCreateBuild(branch, nameDescription());
        // Status id
        ValidationRunStatusID passed = validationRunStatusService.getValidationRunStatus(ValidationRunStatusID.PASSED);
        // Creation of the run
        structureService.newValidationRun(
                ValidationRun.of(
                        build,
                        stamp,
                        0,
                        Signature.of("user"),
                        passed,
                        "Passed test"
                )
        );
    }

    @Test
    public void validationRun_create() throws Exception {
        // Prerequisites
        Branch branch = doCreateBranch();
        ValidationStamp stamp = doCreateValidationStamp(branch, nameDescription());
        Build build = doCreateBuild(branch, nameDescription());
        // Status id
        ValidationRunStatusID passed = validationRunStatusService.getValidationRunStatus(ValidationRunStatusID.PASSED);
        // Creation of the run
        ValidationRun run = asUser().with(branch.getProject().id(), ValidationRunCreate.class).call(() ->
                        structureService.newValidationRun(
                                ValidationRun.of(
                                        build,
                                        stamp,
                                        0,
                                        Signature.of("user"),
                                        passed,
                                        "Passed test"
                                )
                        )
        );
        Entity.isEntityDefined(run, "Validation run is defined");
        assertEquals("Validation run order must be 1", 1, run.getRunOrder());
    }

    private Build doCreateBuild(Branch branch, NameDescription nameDescription) throws Exception {
        return asUser().with(branch.getProject().id(), BuildCreate.class).call(() ->
                        structureService.newBuild(
                                Build.of(
                                        branch,
                                        nameDescription,
                                        Signature.of("user")
                                )
                        )
        );
    }

    private ValidationStamp doCreateValidationStamp(Branch branch, NameDescription nameDescription) throws Exception {
        return asUser().with(branch.getProject().id(), ValidationStampCreate.class).call(() ->
                        structureService.newValidationStamp(
                                ValidationStamp.of(
                                        branch,
                                        nameDescription
                                )
                        )
        );
    }

    private PromotionLevel doCreatePromotionLevel() throws Exception {
        Branch branch = doCreateBranch();
        return doCreatePromotionLevel(branch, nameDescription());
    }

    private PromotionLevel doCreatePromotionLevel(Branch branch, NameDescription nameDescription) throws Exception {
        return asUser().with(branch.getProject().id(), PromotionLevelCreate.class).call(() ->
                        structureService.newPromotionLevel(
                                PromotionLevel.of(
                                        branch, nameDescription
                                )
                        )
        );
    }

    private Branch doCreateBranch() throws Exception {
        Project project = doCreateProject();
        return doCreateBranch(project, nameDescription());
    }

    private Branch doCreateBranch(Project project, NameDescription nameDescription) throws Exception {
        return asUser().with(project.id(), BranchCreate.class).call(() -> structureService.newBranch(
                Branch.of(project, nameDescription)
        ));
    }

    private Project doCreateProject() throws Exception {
        return doCreateProject(nameDescription());
    }

    private Project doCreateProject(NameDescription nameDescription) throws Exception {
        return asUser().with(ProjectCreation.class).call(() -> structureService.newProject(
                Project.of(nameDescription)
        ));
    }

    private int[] doCreateProjects() throws Exception {
        return asUser().with(ProjectCreation.class).call(() -> {
            int[] ids = new int[3];
            int i = 0;
            ids[i++] = structureService.newProject(Project.of(nameDescription())).id();
            ids[i++] = structureService.newProject(Project.of(nameDescription())).id();
            ids[i++] = structureService.newProject(Project.of(nameDescription())).id();
            return ids;
        });
    }

}
