package net.nemerosa.ontrack.ui.resource;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

public abstract class AbstractResourceModule extends SimpleModule {

    private final ResourceContext resourceContext;
    private final Map<Class<?>, BiFunction<BeanSerializerBase, ResourceContext, JsonSerializer<?>>> register =
            new LinkedHashMap<>();

    protected AbstractResourceModule(String name, ResourceContext resourceContext) {
        super(name);
        this.resourceContext = resourceContext;
    }

    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);
        // Collects the bean configurations
        setupResources();
        // Registration
        for (Map.Entry<Class<?>, BiFunction<BeanSerializerBase, ResourceContext, JsonSerializer<?>>> entry : register.entrySet()) {
            context.addBeanSerializerModifier(new ResourceSerializerModifier<>(
                    resourceContext,
                    entry.getKey(),
                    entry.getValue()
            ));
        }
    }

    protected void register(
            Class<?> resourceClass,
            BiFunction<BeanSerializerBase, ResourceContext, JsonSerializer<?>> resourceSerializerFactory) {
        register.put(resourceClass, resourceSerializerFactory);
    }

    protected abstract void setupResources();
}
