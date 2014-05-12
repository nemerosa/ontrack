package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.NameDescription;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.ui.resource.Resource;

import javax.ws.rs.core.Response;
import java.util.List;

public interface StructureAPI {

    List<Project> getProjectList();

    Response newProject(NameDescription nameDescription);

    Resource<Project> getProject(ID id);
}
