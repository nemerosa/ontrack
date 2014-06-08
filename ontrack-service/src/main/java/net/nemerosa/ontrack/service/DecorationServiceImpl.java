package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.extension.api.DecorationExtension;
import net.nemerosa.ontrack.extension.api.ExtensionManager;
import net.nemerosa.ontrack.model.structure.Decoration;
import net.nemerosa.ontrack.model.structure.DecorationService;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DecorationServiceImpl implements DecorationService {

    private final ExtensionManager extensionManager;

    @Autowired
    public DecorationServiceImpl(ExtensionManager extensionManager) {
        this.extensionManager = extensionManager;
    }

    @Override
    public List<Decoration> getDecorations(ProjectEntity entity) {
        // Gets the list of decorators
        // TODO Use parallelStream() with an extension to take into account the current security context
        return extensionManager.getExtensions(DecorationExtension.class).stream()
                // ... and filters per entity
                .filter(decorator -> decorator.getScope().contains(entity.getProjectEntityType()))
                        // ... and gets the decoration
                .map(decorator -> decorator.getDecoration(entity))
                        // ... and excludes the null ones
                .filter(decoration -> decoration != null)
                        // OK
                .collect(Collectors.toList());
    }
}
