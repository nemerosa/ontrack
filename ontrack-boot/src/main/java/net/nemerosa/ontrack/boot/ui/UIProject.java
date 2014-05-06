package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.boot.resource.Resource;
import net.nemerosa.ontrack.model.Branch;
import net.nemerosa.ontrack.model.Project;

import java.util.List;
import java.util.Set;

public interface UIProject {

    Resource<List<Resource<Project>>> projects();

    Resource<Project> project(String id, Set<String> follow);

    Resource<List<Resource<Branch>>> getBranchesForProject(String project);

    Resource<Branch> getBranch(String id);

}
