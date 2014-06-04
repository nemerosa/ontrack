package net.nemerosa.ontrack.boot.resources;

import net.nemerosa.ontrack.boot.ui.ValidationRunController;
import net.nemerosa.ontrack.boot.ui.ValidationStampController;
import net.nemerosa.ontrack.model.security.ValidationRunStatusChange;
import net.nemerosa.ontrack.model.structure.ValidationRun;
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;

import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

public class ValidationRunResourceDecorator extends AbstractResourceDecorator<ValidationRun> {

    protected ValidationRunResourceDecorator() {
        super(ValidationRun.class);
    }

    @Override
    public List<Link> links(ValidationRun validationRun, ResourceContext resourceContext) {
        return resourceContext.links()
                .self(on(ValidationRunController.class).getValidationRun(validationRun.getId()))
                .link(
                        Link.IMAGE_LINK,
                        on(ValidationStampController.class).getValidationStampImage_(validationRun.getValidationStamp().getId())
                )
                .link(
                        "_validationStampLink",
                        on(ValidationStampController.class).getValidationStamp(validationRun.getValidationStamp().getId())
                )
                .link(
                        "_validationRunStatusChange",
                        on(ValidationRunController.class).getValidationRunStatusChangeForm(validationRun.getId()),
                        // Only if transition possible
                        resourceContext.isProjectFunctionGranted(
                                validationRun.getBuild().getBranch().getProject().id(),
                                ValidationRunStatusChange.class
                        ) && !validationRun.getLastStatus().getStatusID().getFollowingStatuses().isEmpty()
                )
                .build();
    }

}
