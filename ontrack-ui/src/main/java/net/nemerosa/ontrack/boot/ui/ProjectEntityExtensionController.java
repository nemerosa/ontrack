package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.extension.api.EntityInformationExtension;
import net.nemerosa.ontrack.extension.api.ExtensionManager;
import net.nemerosa.ontrack.extension.api.ProjectEntityActionExtension;
import net.nemerosa.ontrack.model.support.Action;
import net.nemerosa.ontrack.model.structure.EntityInformation;
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

import java.util.Optional;
import java.util.stream.Collectors;

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
                        .map(x -> x.getAction(getEntity(entityType, id)).map(action -> resolveExtensionAction(x, action)))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList()),
                uri(MvcUriComponentsBuilder.on(getClass()).getActions(entityType, id))
        );
    }

    /**
     * Gets the list of information extensions for an entity
     */
    @RequestMapping(value = "information/{entityType}/{id}", method = RequestMethod.GET)
    public Resources<EntityInformation> getInformation(@PathVariable ProjectEntityType entityType, @PathVariable ID id) {
        return Resources.of(
                extensionManager.getExtensions(EntityInformationExtension.class).stream()
                        .map(x -> x.getInformation(getEntity(entityType, id))
                                .map(o -> new EntityInformation(
                                        entityType,
                                        id,
                                        x.getClass().getName(),
                                        o
                                )))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList()),
                uri(MvcUriComponentsBuilder.on(getClass()).getInformation(entityType, id))
        );
    }

}
