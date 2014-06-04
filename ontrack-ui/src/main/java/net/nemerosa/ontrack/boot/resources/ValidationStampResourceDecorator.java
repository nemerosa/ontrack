package net.nemerosa.ontrack.boot.resources;

import net.nemerosa.ontrack.boot.ui.BranchController;
import net.nemerosa.ontrack.boot.ui.ProjectController;
import net.nemerosa.ontrack.boot.ui.ValidationStampController;
import net.nemerosa.ontrack.model.structure.ValidationStamp;
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;

import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

public class ValidationStampResourceDecorator extends AbstractResourceDecorator<ValidationStamp> {

    protected ValidationStampResourceDecorator() {
        super(ValidationStamp.class);
    }

    @Override
    public List<Link> links(ValidationStamp validationStamp, ResourceContext resourceContext) {
        return resourceContext.links()
                .self(on(ValidationStampController.class).getValidationStamp(validationStamp.getId()))
                        // Branch link
                .link("_branch", on(BranchController.class).getBranch(validationStamp.getBranch().getId()))
                        // Project link
                .link("_project", on(ProjectController.class).getProject(validationStamp.getBranch().getProject().getId()))
                        // Image link
                .link(Link.IMAGE_LINK, on(ValidationStampController.class).getValidationStampImage_(validationStamp.getId()))
                        // TODO Update
                        // TODO Delete
                        // TODO Next validation stamp
                        // TODO Previous validation stamp
                        // OK
                .build();
    }

}
