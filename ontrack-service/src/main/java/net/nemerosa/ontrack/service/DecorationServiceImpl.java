package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.extension.api.DecorationExtension;
import net.nemerosa.ontrack.extension.api.ExtensionManager;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.Decoration;
import net.nemerosa.ontrack.model.structure.DecorationService;
import net.nemerosa.ontrack.model.structure.Decorator;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.service.support.ErrorDecorator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class DecorationServiceImpl implements DecorationService {

    private final ExtensionManager extensionManager;
    private final List<Decorator> builtinDecorators;
    private final SecurityService securityService;
    private final ErrorDecorator errorDecorator = new ErrorDecorator();

    @Autowired
    public DecorationServiceImpl(ExtensionManager extensionManager, List<Decorator> builtinDecorators, SecurityService securityService) {
        this.extensionManager = extensionManager;
        this.builtinDecorators = builtinDecorators.stream()
                .filter(decorator -> !(decorator instanceof DecorationExtension))
                .collect(Collectors.toList());
        this.securityService = securityService;
    }

    @Override
    public List<Decoration> getDecorations(ProjectEntity entity) {
        // Downloading a decoration with the current security context
        Function<Decorator, Decoration> securedDecoratorFunction = securityService.runner(
                decorator -> getDecoration(entity, decorator)
        );
        List<Decoration> decorations = new ArrayList<>();
        // Built-in decorations
        decorations.addAll(
                builtinDecorators.stream()
                        // ... and gets the decoration
                        .map(securedDecoratorFunction)
                                // ... and excludes the null ones
                        .filter(decoration -> decoration != null)
                                // OK
                        .collect(Collectors.toList())
        );
        // Extended decorations
        decorations.addAll(
                extensionManager.getExtensions(DecorationExtension.class)
                        .stream()
                                // ... and filters per entity
                        .filter(decorator -> decorator.getScope().contains(entity.getProjectEntityType()))
                                // ... and gets the decoration
                        .map(securedDecoratorFunction)
                                // ... and excludes the null ones
                        .filter(decoration -> decoration != null)
                                // OK
                        .collect(Collectors.toList())
        );
        // OK
        return decorations;
    }

    /**
     * Gets the decoration for an entity, and returns an "error" decoration in case of problem.
     */
    protected Decoration getDecoration(ProjectEntity entity, Decorator decorator) {
        try {
            return decorator.getDecoration(entity);
        } catch (Exception ex) {
            return errorDecorator.getDecoration(ex);
        }
    }
}
