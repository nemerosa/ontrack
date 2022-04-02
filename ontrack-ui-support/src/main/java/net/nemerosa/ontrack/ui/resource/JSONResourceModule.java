package net.nemerosa.ontrack.ui.resource;

import com.fasterxml.jackson.databind.module.SimpleModule;

import java.util.Collection;

public class JSONResourceModule extends SimpleModule {

    private final ResourceModule resourceModule;
    private final ResourceContext resourceContext;

    public JSONResourceModule(ResourceModule resourceModule, ResourceContext resourceContext) {
        super(resourceModule.getClass().getSimpleName());
        this.resourceModule = resourceModule;
        this.resourceContext = resourceContext;
    }

    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);
        // Registration
        Collection<ResourceDecorator<?>> decorators = resourceModule.decorators();
        if (decorators != null) {
            for (ResourceDecorator<?> resourceDecorator : decorators) {
                context.addBeanSerializerModifier(
                        new ResourceSerializerModifier<>(
                                resourceContext,
                                resourceDecorator
                        )
                );
            }
        }
    }

}
