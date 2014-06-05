package net.nemerosa.ontrack.boot.resources;

import net.nemerosa.ontrack.boot.ui.BranchController;
import net.nemerosa.ontrack.boot.ui.ProjectController;
import net.nemerosa.ontrack.boot.ui.PropertyController;
import net.nemerosa.ontrack.model.security.ProjectEdit;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;

import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

public class ProjectResourceDecorator extends AbstractResourceDecorator<Project> {

    public ProjectResourceDecorator() {
        super(Project.class);
    }

    @Override
    public List<Link> links(Project project, ResourceContext resourceContext) {
        return resourceContext.links()
                .self(on(ProjectController.class).getProject(project.getId()))
                        // List of branches for this project
                .link("_branches", on(BranchController.class).getBranchListForProject(project.getId()))
                        // List of editable properties for this project
                .link("_editableProperties", on(PropertyController.class).getEditableProperties(ProjectEntityType.PROJECT, project.getId()))
                        // Actual properties for this project
                .link("_properties", on(PropertyController.class).getProperties(ProjectEntityType.PROJECT, project.getId()))
                        // Updating the project
                .update(on(ProjectController.class).saveProject(project.getId(), null), ProjectEdit.class, project.id())
                        // TODO Delete link
                        // TODO View link
                .build();
    }
}
