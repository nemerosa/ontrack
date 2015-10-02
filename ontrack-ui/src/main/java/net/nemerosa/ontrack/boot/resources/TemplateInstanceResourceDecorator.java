package net.nemerosa.ontrack.boot.resources;

import net.nemerosa.ontrack.boot.ui.BranchController;
import net.nemerosa.ontrack.model.structure.TemplateInstance;
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Component
public class TemplateInstanceResourceDecorator extends AbstractResourceDecorator<TemplateInstance> {

    public TemplateInstanceResourceDecorator() {
        super(TemplateInstance.class);
    }

    @Override
    public List<Link> links(TemplateInstance resource, ResourceContext resourceContext) {
        return resourceContext.links()
                .link(
                        "_template",
                        on(BranchController.class).getBranch(resource.getTemplateDefinitionId())
                )
                        // OK
                .build();
    }
}
