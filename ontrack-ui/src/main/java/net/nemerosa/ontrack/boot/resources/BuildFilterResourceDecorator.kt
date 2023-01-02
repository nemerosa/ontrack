package net.nemerosa.ontrack.boot.resources;

import net.nemerosa.ontrack.boot.ui.BuildFilterController;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterResource;
import net.nemerosa.ontrack.model.security.BranchFilterMgt;
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Component
public class BuildFilterResourceDecorator extends AbstractResourceDecorator<BuildFilterResource> {

    public BuildFilterResourceDecorator() {
        super(BuildFilterResource.class);
    }

    @Override
    public List<Link> links(BuildFilterResource resource, ResourceContext resourceContext) {
        return resourceContext.links()
                // Update
                .link(
                        Link.UPDATE,
                        on(BuildFilterController.class).getEditionForm(resource.getBranch().getId(), resource.getName())
                )
                        // Sharing
                .link(
                        "_share",
                        on(BuildFilterController.class).getEditionForm(resource.getBranch().getId(), resource.getName()),
                        resourceContext.isProjectFunctionGranted(resource.getBranch().projectId(), BranchFilterMgt.class)
                                && !resource.isShared()
                )
                        // Delete
                .link(
                        Link.DELETE,
                        on(BuildFilterController.class).deleteFilter(resource.getBranch().getId(), resource.getName())
                )
                        // OK
                .build();
    }
}
