package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.NameDescription;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.ui.resource.Resource;

import java.util.List;

public interface StructureAPI {

    List<Project> getProjectList();

    Resource<Project> newProject(NameDescription nameDescription);

    Resource<Project> getProject(ID id);
}
