package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.boot.resource.Resource;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static net.nemerosa.ontrack.boot.resource.Resource.link;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromMethodCall;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Component
public class UIResourceAssembler implements ResourceAssembler {

    private final ApplicationContext applicationContext;

    @Autowired
    public UIResourceAssembler(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    private UIProjectController uiProject() {
        return applicationContext.getBean(UIProjectController.class);
    }

    @Override
    public Resource<Project> toProjectResource(Project project, Set<String> follow) {
        return Resource.of(project)
                .self(link(fromMethodCall(on(UIProjectController.class).project(project.getId(), follow))))
                        // Branches
                .link("branches",
                        link(fromMethodCall(on(UIProjectController.class).getBranchesForProject(project.getId()))),
                        () -> uiProject().getBranchesForProject(project.getId())
                )
                // FIXME Promotion levels
                // FIXME Validation stamps
                ;
    }

    @Override
    public Resource<Branch> toBranchResource(Branch branch) {
        return Resource.of(branch)
                .self(link(fromMethodCall(on(UIProjectController.class).getBranch(branch.getId()))))
                // FIXME Project from branch
                // FIXME Builds
                ;
    }

    @Override
    public Resource<List<Resource<Project>>> toProjectCollectionResource(List<Project> projects) {
        return Resource.of(
                projects.stream()
                        .map(p -> toProjectResource(p, Collections.emptySet()))
                        .collect(Collectors.toList())
        )
                .self(link(fromMethodCall(on(UIProjectController.class).projects())));
    }

    @Override
    public Resource<List<Resource<Branch>>> toBranchCollectionResource(String project, List<Branch> branches) {
        return Resource.of(
                branches
                        .stream()
                        .map(this::toBranchResource)
                        .collect(Collectors.toList())
        )
                .self(link(fromMethodCall(on(UIProjectController.class).getBranchesForProject(project))))
                ;
    }
}
