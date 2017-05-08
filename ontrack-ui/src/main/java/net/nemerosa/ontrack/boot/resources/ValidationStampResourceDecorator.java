package net.nemerosa.ontrack.boot.resources;

import net.nemerosa.ontrack.boot.ui.*;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.security.ValidationStampDelete;
import net.nemerosa.ontrack.model.security.ValidationStampEdit;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.structure.ValidationStamp;
import net.nemerosa.ontrack.ui.resource.AbstractLinkResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.LinkDefinition;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static net.nemerosa.ontrack.ui.resource.LinkDefinitions.*;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Component
public class ValidationStampResourceDecorator extends AbstractLinkResourceDecorator<ValidationStamp> {

    public ValidationStampResourceDecorator() {
        super(ValidationStamp.class);
    }

    @Override
    protected Iterable<LinkDefinition<ValidationStamp>> getLinkDefinitions() {
        return Arrays.asList(
                link(
                        Link.SELF,
                        validationStamp -> on(ValidationStampController.class).getValidationStamp(validationStamp.getId())
                ),
                // Branch link
                link(
                        "_branch",
                        validationStamp -> on(BranchController.class).getBranch(validationStamp.getBranch().getId())
                ),
                // Project link
                link(
                        "_project",
                        validationStamp -> on(ProjectController.class).getProject(validationStamp.getBranch().getProject().getId())
                ),
                // Image link
                link(
                        Link.IMAGE_LINK,
                        validationStamp -> on(ValidationStampController.class).getValidationStampImage_(null, validationStamp.getId())
                ),
                // Update link
                link(
                        Link.UPDATE,
                        validationStamp -> on(ValidationStampController.class).updateValidationStampForm(validationStamp.getId()),
                        withProjectFn(ValidationStampEdit.class)
                ),
                // Delete link
                link(
                        Link.DELETE,
                        validationStamp -> on(ValidationStampController.class).deleteValidationStamp(validationStamp.getId()),
                        withProjectFn(ValidationStampDelete.class)
                ),
                // TODO Next validation stamp
                // TODO Previous validation stamp
                // Actual properties for this validation stamp
                link(
                        "_properties",
                        validationStamp -> on(PropertyController.class).getProperties(ProjectEntityType.VALIDATION_STAMP, validationStamp.getId())
                ),
                // Decorations
                link(
                        "_decorations",
                        validationStamp -> on(DecorationsController.class).getDecorations(validationStamp.getProjectEntityType(), validationStamp.getId())
                ),
                // List of runs
                link(
                        "_runs",
                        validationStamp -> on(ValidationRunController.class).getValidationRunsForValidationStamp(validationStamp.getId(), 0, 10)
                ),
                // Events
                link(
                        "_events",
                        validationStamp -> on(EventController.class).getEvents(validationStamp.getProjectEntityType(), validationStamp.getId(), 0, 10)
                ),
                // Bulk update
                link(
                        "_bulkUpdate",
                        validationStamp -> on(ValidationStampController.class).bulkUpdate(validationStamp.getId()),
                        withGlobalFn(GlobalSettings.class)
                ),
                // Page
                page()
        );
    }

}
