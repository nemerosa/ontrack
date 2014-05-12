package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.structure.NameDescription;
import net.nemerosa.ontrack.model.structure.Project;

import java.util.List;

public interface StructureAPI {

    List<Project> getProjectList();

    Project newProject(NameDescription nameDescription);
}
