package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.boot.resource.Resource;
import net.nemerosa.ontrack.model.Branch;
import net.nemerosa.ontrack.model.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Set;

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

    private UIBranch uiBranch() {
        return applicationContext.getBean(UIBranch.class);
    }

    @Override
    public Resource<Project> toProjectResource(Project project, Set<String> follow) {
        return Resource.of(project)
                .self(link(fromMethodCall(on(UIProject.class).project(project.getId(), follow))))
                        // Branches
                .link("branches",
                        link(fromMethodCall(on(UIBranch.class).getBranchesForProject(project.getId()))),
                        () -> uiBranch().getBranchesForProject(project.getId())
                )
                // FIXME Promotion levels
                // FIXME Validation stamps
                ;
    }

    @Override
    public Resource<Branch> toBranchResource(Branch branch) {
        return Resource.of(branch)
                .self(link(fromMethodCall(on(UIBranch.class).getBranch(branch.getId()))))
                // FIXME Project from branch
                // FIXME Builds
                ;
    }
}
