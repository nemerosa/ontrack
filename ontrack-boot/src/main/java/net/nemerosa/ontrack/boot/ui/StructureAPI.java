package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.NameDescription;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.ui.resource.Resource;
import net.nemerosa.ontrack.ui.resource.ResourceCollection;

public interface StructureAPI {

    ResourceCollection<Project> getProjectList();

    Resource<Project> newProject(NameDescription nameDescription);

    Form newProjectForm();

    Resource<Project> saveProject(ID id, NameDescription nameDescription);

    Resource<Project> getProject(ID id);
}
