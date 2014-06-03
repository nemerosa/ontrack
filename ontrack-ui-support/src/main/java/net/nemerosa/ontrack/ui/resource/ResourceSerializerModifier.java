package net.nemerosa.ontrack.ui.resource;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;

public class ResourceSerializerModifier<T> extends BeanSerializerModifier {

    private final ResourceContext resourceContext;
    private final ResourceDecorator<T> resourceDecorator;

    public ResourceSerializerModifier(ResourceContext resourceContext, ResourceDecorator<T> resourceDecorator) {
        this.resourceContext = resourceContext;
        this.resourceDecorator = resourceDecorator;
    }

    public JsonSerializer<?> modifySerializer(
            SerializationConfig config,
            BeanDescription beanDesc,
            JsonSerializer<?> serializer) {
        if (resourceDecorator.appliesFor(beanDesc.getBeanClass())) {
            return new ResourceDecoratorSerializer<>((BeanSerializerBase) serializer, resourceDecorator, resourceContext);
        } else {
            return super.modifySerializer(config, beanDesc, serializer);
        }
    }

}
