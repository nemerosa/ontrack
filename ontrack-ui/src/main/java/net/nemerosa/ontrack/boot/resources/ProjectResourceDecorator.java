package net.nemerosa.ontrack.boot.resources;

import net.nemerosa.ontrack.boot.ui.ProjectController;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;

import java.util.Arrays;
import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

public class ProjectResourceDecorator extends AbstractResourceDecorator<Project> {

    public ProjectResourceDecorator() {
        super(Project.class);
    }

    @Override
    public List<Link> links(Project project, ResourceContext resourceContext) {
        return Arrays.asList(
                Link.of(Link.SELF, resourceContext.uri(on(ProjectController.class).getProject(project.getId())))
        );
    }
}
