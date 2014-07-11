package net.nemerosa.ontrack.boot.resources;

import net.nemerosa.ontrack.boot.ui.BuildFilterController;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterResource;
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;

import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

public class BuildFilterResourceDecorator extends AbstractResourceDecorator<BuildFilterResource> {

    public BuildFilterResourceDecorator() {
        super(BuildFilterResource.class);
    }

    @Override
    public List<Link> links(BuildFilterResource resource, ResourceContext resourceContext) {
        return resourceContext.links()
                // TODO Update
                // Delete
                .link(Link.DELETE, on(BuildFilterController.class).deleteFilter(resource.getBranchId(), resource.getName()))
                        // OK
                .build();
    }
}
