package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.NameDescription;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.ui.resource.Resource;
import net.nemerosa.ontrack.ui.resource.ResourceCollection;

public interface StructureAPI {

    // Projects

    ResourceCollection<Project> getProjectList();

    Resource<Project> newProject(NameDescription nameDescription);

    Form newProjectForm();

    Resource<Project> getProject(ID id);

    Form saveProjectForm(ID id);

    Resource<Project> saveProject(ID id, NameDescription nameDescription);

    // Branches

    ResourceCollection<Branch> getBranchListForProject(ID projectId);

    Form newBranchForm();

    Resource<Branch> newBranch(ID projectId, NameDescription nameDescription);

    Resource<Branch> getBranch(ID branchId);

}
