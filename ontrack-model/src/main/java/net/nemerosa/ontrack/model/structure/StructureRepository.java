package net.nemerosa.ontrack.model.structure;

import net.nemerosa.ontrack.model.annotations.*;

import java.util.List;

public interface StructureRepository {

    // Projects

    @GlobalGrant(ProjectCreation.class)
    Project newProject(Project project);

    @GlobalGrant(ProjectList.class)
    List<Project> getProjectList();

    @ProjectGrant(fn = ProjectView.class, before = "id")
    Project getProject(ID projectId);

    @ProjectGrant(fn = ProjectEdit.class, before = "id")
    void saveProject(@ProjectGrantTarget Project project);

    // Branches

    @ProjectGrant(fn = ProjectView.class, after = "project.id")
    Branch getBranch(ID branchId);

    @ProjectGrant(fn = ProjectView.class, before = "")
    List<Branch> getBranchesForProject(@ProjectGrantTarget ID projectId);

    @ProjectGrant(fn = ProjectEdit.class, before = "project.id")
    Branch newBranch(@ProjectGrantTarget Branch branch);

    // Builds

    @ProjectGrant(fn = ProjectBuild.class, before = "branch.project.id")
    Build newBuild(@ProjectGrantTarget Build build);

    @ProjectGrant(fn = ProjectEdit.class, before = "branch.project.id")
    Build saveBuild(@ProjectGrantTarget Build build);
}
