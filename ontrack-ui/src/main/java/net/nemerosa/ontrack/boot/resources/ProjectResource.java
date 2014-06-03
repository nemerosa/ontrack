package net.nemerosa.ontrack.boot.resources;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import net.nemerosa.ontrack.boot.ui.ProjectController;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.ui.resource.AbstractModelResource;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;

import java.io.IOException;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

public class ProjectResource extends AbstractModelResource<Project> {

    protected ProjectResource(BeanSerializerBase src, ResourceContext resourceContext) {
        super(src, resourceContext);
    }

    @Override
    protected void additionalFields(Project project, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeObjectField(Link.SELF, uri(on(ProjectController.class).getProject(project.getId())));
        // TODO Actions?
    }
}
