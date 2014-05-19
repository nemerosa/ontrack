package net.nemerosa.ontrack.service.security;

import net.nemerosa.ontrack.it.AbstractITTestSupport;
import net.nemerosa.ontrack.model.security.ProjectCreation;
import net.nemerosa.ontrack.model.security.ProjectEdit;
import net.nemerosa.ontrack.model.security.ProjectList;
import net.nemerosa.ontrack.model.security.ProjectView;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.StructureService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class StructureServiceTest extends AbstractITTestSupport {

    @Autowired
    private StructureService structureService;

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
