package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.boot.resource.Resource;
import net.nemerosa.ontrack.model.Project;
import org.springframework.stereotype.Component;

import static net.nemerosa.ontrack.boot.resource.Link.link;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.fromMethodCall;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Component
public class UIResourceAssembler implements ResourceAssembler {

    @Override
    public Resource<Project> toProjectResource(Project project) {
        return Resource.of(project)
                .self(link(fromMethodCall(on(UITop.class).project(project.getId()))))
                // FIXME Branches
                // FIXME Promotion levels
                // FIXME Validation stamps
                ;
    }
}
