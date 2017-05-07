package net.nemerosa.ontrack.boot.resources;

import net.nemerosa.ontrack.boot.ui.ValidationStampFilterController;
import net.nemerosa.ontrack.model.security.GlobalSettings;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.structure.ValidationStampFilter;
import net.nemerosa.ontrack.model.structure.ValidationStampFilterScope;
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Component
public class ValidationStampFilterResourceDecorator extends AbstractResourceDecorator<ValidationStampFilter> {

    public ValidationStampFilterResourceDecorator() {
        super(ValidationStampFilter.class);
    }

    @Override
    public List<Link> links(ValidationStampFilter resource, ResourceContext resourceContext) {
        // Scope of the validation stamp filter
        boolean canUpdate;
        if (resource.getProject() != null) {
            canUpdate = resourceContext.isProjectFunctionGranted(resource.getProject(), ProjectConfig.class);
        } else if (resource.getBranch() != null) {
            canUpdate = resourceContext.isProjectFunctionGranted(resource.getBranch(), ProjectConfig.class);
        } else {
            canUpdate = resourceContext.isGlobalFunctionGranted(GlobalSettings.class);
        }
        // Links
        return resourceContext.links()
                // Update if authorized
                .link(
                        Link.UPDATE,
                        on(ValidationStampFilterController.class).getValidationStampFilterUpdateForm(resource.getId()),
                        canUpdate
                )
                // Delete if authorized
                .link(
                        Link.DELETE,
                        on(ValidationStampFilterController.class).deleteValidationStampFilter(resource.getId()),
                        canUpdate
                )
                // Share at project level
                .link(
                        "_shareAtProject",
                        on(ValidationStampFilterController.class).shareValidationStampFilterAtProject(resource.getId()),
                        resource.getScope() == ValidationStampFilterScope.BRANCH && canUpdate
                )
                // Share at global level
                .link(
                        "_shareAtGlobal",
                        on(ValidationStampFilterController.class).shareValidationStampFilterAtGlobal(resource.getId()),
                        (resource.getScope() == ValidationStampFilterScope.PROJECT || resource.getScope() == ValidationStampFilterScope.BRANCH) &&
                                resourceContext.isGlobalFunctionGranted(GlobalSettings.class)
                )
                // OK
                .build();
    }
}
