package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.extension.api.ExtensionManager;
import net.nemerosa.ontrack.extension.api.ProjectEntityActionExtension;
import net.nemerosa.ontrack.model.security.Action;
import net.nemerosa.ontrack.model.structure.ID;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.model.structure.StructureService;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

/**
 * Controller used to get extensions on project entities.
 */
@RestController
@RequestMapping("/extensions/entity")
public class ProjectEntityExtensionController extends AbstractProjectEntityController {

    private final ExtensionManager extensionManager;

    @Autowired
    public ProjectEntityExtensionController(StructureService structureService, ExtensionManager extensionManager) {
        super(structureService);
        this.extensionManager = extensionManager;
    }

    /**
     * Gets the list of actions for an entity
     */
    @RequestMapping(value = "actions/{entityType}/{id}", method = RequestMethod.GET)
    public Resources<Action> getActions(@PathVariable ProjectEntityType entityType, @PathVariable ID id) {
        return Resources.of(
                extensionManager.getExtensions(ProjectEntityActionExtension.class).stream()
                        .map(x -> x.getAction(getEntity(entityType, id)))
                        .filter(action -> action != null),
                uri(MvcUriComponentsBuilder.on(getClass()).getActions(entityType, id))
        );
    }

}
