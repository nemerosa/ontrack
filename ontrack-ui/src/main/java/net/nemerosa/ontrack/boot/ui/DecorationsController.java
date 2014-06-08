package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@RestController
@RequestMapping("/decorations")
public class DecorationsController extends AbstractProjectEntityController {

    private final DecorationService decorationService;

    @Autowired
    public DecorationsController(StructureService structureService, DecorationService decorationService) {
        super(structureService);
        this.decorationService = decorationService;
    }

    /**
     * Decorations for an entity.
     * <p/>
     * TODO Makes this call asynchronous since some decorations might take a long time to be resolved
     */
    @RequestMapping(value = "{entityType}/{id}", method = RequestMethod.GET)
    public Resources<Decoration> getDecorations(@PathVariable ProjectEntityType entityType, @PathVariable ID id) {
        return Resources.of(
                decorationService.getDecorations(getEntity(entityType, id)),
                uri(on(getClass()).getDecorations(entityType, id))
        );
    }

}
