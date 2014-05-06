package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.boot.resource.Resource;
import net.nemerosa.ontrack.model.Branch;
import net.nemerosa.ontrack.model.Project;

import java.util.List;
import java.util.Set;

public interface ResourceAssembler {

    Resource<Project> toProjectResource(Project project, Set<String> follow);

    Resource<Branch> toBranchResource(Branch branch);

    Resource<List<Resource<Project>>> toProjectCollectionResource(List<Project> projects);

    Resource<List<Resource<Branch>>> toBranchCollectionResource(String project, List<Branch> branches);
}
