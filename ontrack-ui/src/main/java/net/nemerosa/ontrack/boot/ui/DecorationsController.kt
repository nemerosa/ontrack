package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.Callable;

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
     */
    @RequestMapping(value = "{entityType}/{id}", method = RequestMethod.GET)
    public Callable<Resources<Decoration<?>>> getDecorations(@PathVariable ProjectEntityType entityType, @PathVariable ID id) {
        // Gets the current request attributes
        RequestAttributes attributes = RequestContextHolder.currentRequestAttributes();
        return () -> {
            RequestContextHolder.setRequestAttributes(attributes);
            try {
                return Resources.of(
                        decorationService.getDecorations(getEntity(entityType, id)),
                        uri(on(getClass()).getDecorations(entityType, id))
                ).forView(Decoration.class);
            } finally {
                RequestContextHolder.resetRequestAttributes();
            }
        };
    }

}
