package net.nemerosa.ontrack.boot.resources;

import net.nemerosa.ontrack.boot.ui.*;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.structure.ValidationRun;
import net.nemerosa.ontrack.ui.resource.AbstractLinkResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.LinkDefinition;
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
                // Run info
                link(
                        "_runInfo",
                        validationRun -> on(RunInfoController.class).getRunInfo(validationRun.getRunnableEntityType(), validationRun.id())
                ),
                // Extra information
                link(
                        "_extra",
                        validationRun -> on(ProjectEntityExtensionController.class).getInformation(ProjectEntityType.VALIDATION_RUN, validationRun.getId())
                ),
                // Page
                page()
        );
    }

}
