package net.nemerosa.ontrack.boot.resources;

import net.nemerosa.ontrack.boot.ui.DecorationsController;
import net.nemerosa.ontrack.boot.ui.PropertyController;
import net.nemerosa.ontrack.boot.ui.ValidationRunController;
import net.nemerosa.ontrack.boot.ui.ValidationStampController;
import net.nemerosa.ontrack.model.security.ValidationRunStatusChange;
import net.nemerosa.ontrack.model.structure.ValidationRun;
import net.nemerosa.ontrack.ui.resource.AbstractLinkResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.LinkDefinition;
import net.nemerosa.ontrack.ui.resource.ResourceContext;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static net.nemerosa.ontrack.ui.resource.LinkDefinitions.*;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Component
public class ValidationRunResourceDecorator extends AbstractLinkResourceDecorator<ValidationRun> {

    public ValidationRunResourceDecorator() {
        super(ValidationRun.class);
    }

    @Override
    protected Iterable<LinkDefinition<ValidationRun>> getLinkDefinitions() {
        return Arrays.asList(
                self(validationRun -> on(ValidationRunController.class).getValidationRun(validationRun.getId())),
                link(
                        Link.IMAGE_LINK,
                        validationRun -> on(ValidationStampController.class).getValidationStampImage_(null, validationRun.getValidationStamp().getId())
                ),
                link(
                        "_validationStampLink",
                        validationRun -> on(ValidationStampController.class).getValidationStamp(validationRun.getValidationStamp().getId())
                ),
                link(
                        "_validationRunStatusChange",
                        validationRun -> on(ValidationRunController.class).getValidationRunStatusChangeForm(validationRun.getId()),
                        // Only if transition possible
                        (ValidationRun validationRun, ResourceContext resourceContext) ->
                                resourceContext.isProjectFunctionGranted(
                                        validationRun.getBuild().getBranch().getProject().id(),
                                        ValidationRunStatusChange.class
                                ) && !validationRun.getLastStatus().getStatusID().getFollowingStatuses().isEmpty()
                ),
                // Actual properties for this entity
                link(
                        "_properties",
                        validationRun -> on(PropertyController.class).getProperties(validationRun.getProjectEntityType(), validationRun.getId())
                ),
                // Decorations
                link(
                        "_decorations",
                        validationRun -> on(DecorationsController.class).getDecorations(validationRun.getProjectEntityType(), validationRun.getId())
                ),
                // Page
                page()
        );
    }

}
