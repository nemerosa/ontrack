package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.common.Document;
import net.nemerosa.ontrack.it.AbstractServiceTestSupport;
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

public class StructureServiceIT extends AbstractServiceTestSupport {

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
    public void newPromotionLevel() throws Exception {
        PromotionLevel promotionLevel = doCreatePromotionLevel();
        assertNotNull(promotionLevel);
        PromotionLevel pl = asUser().with(promotionLevel, ProjectView.class).call(() -> structureService.getPromotionLevel(promotionLevel.getId()));
        assertEquals(promotionLevel, pl);
    }

    @Test
    public void newValidationStamp() throws Exception {
        ValidationStamp validationStamp = doCreateValidationStamp();
        assertNotNull(validationStamp);
        ValidationStamp vs = asUser().with(validationStamp, ProjectView.class).call(() -> structureService.getValidationStamp(validationStamp.getId()));
        assertEquals(validationStamp, vs);
    }

    @Test
    public void newBuild() throws Exception {
        Build build = doCreateBuild();
        assertNotNull(build);
        Build b = asUser().with(build, ProjectView.class).call(() -> structureService.getBuild(build.getId()));
        assertEquals(build, b);
    }

    @Test
    public void getProjectList_all() throws Exception {
        int[] ids = doCreateProjects();
        List<Project> list = asUser().with(ProjectList.class).call(structureService::getProjectList);
        assertTrue(list.size() >= 3);
        assertTrue(list.stream().anyMatch(p -> p.id() == ids[0]));
        assertTrue(list.stream().anyMatch(p -> p.id() == ids[1]));
        assertTrue(list.stream().anyMatch(p -> p.id() == ids[2]));
    }

    @Test
    public void getProjectList_filtered() throws Exception {
        grantViewToAll(false);
        int[] ids = doCreateProjects();
        List<Project> list = asUser()
                .with(ids[0], ProjectView.class)
                .with(ids[1], ProjectEdit.class)
                .call(structureService::getProjectList);
        assertEquals(2, list.size());
        assertTrue(list.stream().anyMatch(p -> p.id() == ids[0]));
        assertTrue(list.stream().anyMatch(p -> p.id() == ids[1]));
        assertTrue(list.stream().noneMatch(p -> p.id() == ids[2]));
    }

    @Test
    public void getProjectList_none() throws Exception {
        grantViewToAll(false);
        doCreateProjects();
        List<Project> list = asUser()
                .call(structureService::getProjectList);
        assertEquals(0, list.size());
    }

    @Test
    public void promotionLevel_image_none() throws Exception {
        PromotionLevel promotionLevel = doCreatePromotionLevel();
        Document image = view(promotionLevel, () -> structureService.getPromotionLevelImage(promotionLevel.getId()));
        assertTrue("No image", image.isEmpty());
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
    public void promotionLevel_image_empty() throws Exception {
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
        assertTrue("Empty image", d.isEmpty());
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

    private int[] doCreateProjects() throws Exception {
        return asUser().with(ProjectCreation.class).call(() -> {
            int[] ids = new int[3];
            int i = 0;
            ids[i++] = structureService.newProject(Project.of(nameDescription())).id();
            ids[i++] = structureService.newProject(Project.of(nameDescription())).id();
            //noinspection UnusedAssignment
            ids[i++] = structureService.newProject(Project.of(nameDescription())).id();
            return ids;
        });
    }

}
