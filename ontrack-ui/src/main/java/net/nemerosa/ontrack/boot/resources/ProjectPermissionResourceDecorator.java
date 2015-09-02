package net.nemerosa.ontrack.boot.resources;

import net.nemerosa.ontrack.boot.ui.PermissionController;
import net.nemerosa.ontrack.model.security.ProjectPermission;
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Component
public class ProjectPermissionResourceDecorator extends AbstractResourceDecorator<ProjectPermission> {

    public ProjectPermissionResourceDecorator() {
        super(ProjectPermission.class);
    }

    @Override
    public List<Link> links(ProjectPermission permission, ResourceContext resourceContext) {
        return resourceContext.links()
                // Update
                .link(Link.UPDATE, on(PermissionController.class).saveProjectPermission(permission.getProjectId(), permission.getTarget().getType(), permission.getTarget().getId(), null))
                        // Delete
                .link(Link.DELETE, on(PermissionController.class).deleteProjectPermission(permission.getProjectId(), permission.getTarget().getType(), permission.getTarget().getId()))
                        // OK
                .build();
    }
}
