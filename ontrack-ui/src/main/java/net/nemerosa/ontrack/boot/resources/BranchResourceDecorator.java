package net.nemerosa.ontrack.boot.resources;

import net.nemerosa.ontrack.boot.ui.BranchController;
import net.nemerosa.ontrack.boot.ui.ProjectController;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;

import java.util.Arrays;
import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

public class BranchResourceDecorator extends AbstractResourceDecorator<Branch> {

    protected BranchResourceDecorator() {
        super(Branch.class);
    }

    @Override
    public List<Link> links(Branch branch, ResourceContext resourceContext) {
        return Arrays.asList(
                Link.of(Link.SELF, resourceContext.uri(on(BranchController.class).getBranch(branch.getId()))),
                Link.of("_projectLink", resourceContext.uri(on(ProjectController.class).getProject(branch.getProject().getId())))
                // TODO Actions?
        );
    }

}
