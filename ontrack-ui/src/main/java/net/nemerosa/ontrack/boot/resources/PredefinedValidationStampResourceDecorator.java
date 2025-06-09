package net.nemerosa.ontrack.boot.resources;

import net.nemerosa.ontrack.boot.ui.PredefinedValidationStampController;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.structure.PredefinedValidationStamp;
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Component
public class PredefinedValidationStampResourceDecorator extends AbstractResourceDecorator<PredefinedValidationStamp> {

    public PredefinedValidationStampResourceDecorator() {
        super(PredefinedValidationStamp.class);
    }

    @Override
    public List<Link> links(PredefinedValidationStamp validationStamp, ResourceContext resourceContext) {
        return resourceContext.links()
                .self(on(PredefinedValidationStampController.class).getValidationStamp(validationStamp.getId()))
                        // Image link
                .link(Link.IMAGE_LINK, on(PredefinedValidationStampController.class).getValidationStampImage(validationStamp.getId()))
                        // Delete link
                .link(Link.DELETE, on(PredefinedValidationStampController.class).deleteValidationStamp(validationStamp.getId()), GlobalSettings.class)
                        // OK
                .build();
    }

}
