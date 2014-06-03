package net.nemerosa.ontrack.ui.resource;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import net.nemerosa.ontrack.ui.resource.ResourceContext;

import java.util.function.BiFunction;

public class ResourceSerializerModifier<T> extends BeanSerializerModifier {

    private final ResourceContext resourceContext;
    private final Class<T> resourceClass;
    private final BiFunction<BeanSerializerBase, ResourceContext, JsonSerializer<?>> resourceSerializerFactory;

    public ResourceSerializerModifier(
            ResourceContext resourceContext,
            Class<T> resourceClass,
            BiFunction<BeanSerializerBase, ResourceContext, JsonSerializer<?>> resourceSerializerFactory) {
        this.resourceContext = resourceContext;
        this.resourceClass = resourceClass;
        this.resourceSerializerFactory = resourceSerializerFactory;
    }

    public JsonSerializer<?> modifySerializer(
            SerializationConfig config,
            BeanDescription beanDesc,
            JsonSerializer<?> serializer) {
        if (resourceClass.isAssignableFrom(beanDesc.getBeanClass())) {
            return resourceSerializerFactory.apply((BeanSerializerBase) serializer, resourceContext);
        } else {
            return super.modifySerializer(config, beanDesc, serializer);
        }
    }

}
