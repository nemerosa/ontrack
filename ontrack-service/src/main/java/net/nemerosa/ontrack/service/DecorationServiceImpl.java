package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.common.BaseException;
import net.nemerosa.ontrack.extension.api.DecorationExtension;
import net.nemerosa.ontrack.extension.api.ExtensionManager;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.Decoration;
import net.nemerosa.ontrack.model.structure.DecorationService;
import net.nemerosa.ontrack.model.structure.Decorator;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class DecorationServiceImpl implements DecorationService {

    private final ExtensionManager extensionManager;
    private final SecurityService securityService;

    @Autowired
    public DecorationServiceImpl(ExtensionManager extensionManager, SecurityService securityService) {
        this.extensionManager = extensionManager;
        this.securityService = securityService;
    }

    @Override
    public List<Decoration<?>> getDecorations(ProjectEntity entity) {
        // Downloading a decoration with the current security context
        Function<Decorator, Stream<Decoration<?>>> securedDecoratorFunction = securityService.runner(
                decorator -> getDecorations(entity, decorator).stream()
        );
        // OK
        return extensionManager.getExtensions(DecorationExtension.class)
                .stream()
                        // ... and filters per entity
                .filter(decorator -> decorator.getScope().contains(entity.getProjectEntityType()))
                        // ... and gets the decoration
                .flatMap(securedDecoratorFunction)
                        // OK
                .collect(Collectors.toList());
    }

    /**
     * Gets the decoration for an entity, and returns an "error" decoration in case of problem.
     */
    protected <T> List<? extends Decoration> getDecorations(ProjectEntity entity, Decorator<T> decorator) {
        try {
            return decorator.getDecorations(entity);
        } catch (Exception ex) {
            return Collections.singletonList(
                    Decoration.error(decorator, getErrorMessage(ex))
            );
        }
    }

    /**
     * Decoration error message
     */
    protected String getErrorMessage(Exception ex) {
        if (ex instanceof BaseException) {
            return ex.getMessage();
        } else {
            return "Problem while getting decoration";
        }
    }
}
