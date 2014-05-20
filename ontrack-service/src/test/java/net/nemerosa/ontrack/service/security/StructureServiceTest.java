package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.it.AbstractITTestSupport;
import net.nemerosa.ontrack.model.security.*;
import net.nemerosa.ontrack.model.structure.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;

public class StructureServiceTest extends AbstractITTestSupport {

    @Autowired
    private StructureService structureService;

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
