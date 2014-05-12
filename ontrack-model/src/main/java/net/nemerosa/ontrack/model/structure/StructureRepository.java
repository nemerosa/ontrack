package net.nemerosa.ontrack.model.structure;

import java.util.List;

public interface StructureRepository {

    Project newProject(Project project);

    List<Project> getProjectList();

    Project getProject(ID projectId);
}
