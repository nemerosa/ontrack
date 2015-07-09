package net.nemerosa.ontrack.extension.general;

import net.nemerosa.ontrack.model.structure.StructureService;
import net.nemerosa.ontrack.ui.resource.AbstractResourceModule;
import net.nemerosa.ontrack.ui.resource.ResourceDecorator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;

/**
 * Collection of {@link net.nemerosa.ontrack.ui.resource.ResourceDecorator} for the <code>general</code> extension.
 */
// FIXME @Component
public class GeneralExtensionResourceModule extends AbstractResourceModule {

    private final StructureService structureService;

    @Autowired
    public GeneralExtensionResourceModule(StructureService structureService) {
        this.structureService = structureService;
    }

    @Override
    public Collection<ResourceDecorator<?>> decorators() {
        return Collections.singletonList(
                new BuildLinkPropertyItemResourceDecorator(structureService)
        );
    }
}
