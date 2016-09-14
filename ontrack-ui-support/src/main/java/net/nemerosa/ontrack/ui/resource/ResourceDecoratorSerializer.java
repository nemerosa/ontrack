package net.nemerosa.ontrack.ui.resource;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.impl.ObjectIdWriter;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.util.Set;

import static java.lang.String.format;

public class ResourceDecoratorSerializer<T> extends BeanSerializerBase {

    private final ResourceContext resourceContext;
    private final ResourceDecorator<T> resourceDecorator;

    public ResourceDecoratorSerializer(BeanSerializerBase serializer, ResourceDecorator<T> resourceDecorator, ResourceContext resourceContext) {
        super(serializer);
        this.resourceDecorator = resourceDecorator;
        this.resourceContext = resourceContext;
    }

    protected ResourceDecoratorSerializer(BeanSerializerBase src, ObjectIdWriter objectIdWriter, ResourceContext resourceContext, ResourceDecorator<T> resourceDecorator) {
        super(src, objectIdWriter);
        this.resourceContext = resourceContext;
        this.resourceDecorator = resourceDecorator;
    }

    public ResourceDecoratorSerializer(BeanSerializerBase src, Set<String> toIgnore, ResourceContext resourceContext, ResourceDecorator<T> resourceDecorator) {
        super(src, toIgnore);
        this.resourceContext = resourceContext;
        this.resourceDecorator = resourceDecorator;
    }

    @Override
    public BeanSerializerBase withObjectIdWriter(ObjectIdWriter objectIdWriter) {
        return new ResourceDecoratorSerializer<>(
                this,
                objectIdWriter,
                resourceContext,
                resourceDecorator
        );
    }

    @Override
    protected BeanSerializerBase withIgnorals(Set<String> toIgnore) {
        return new ResourceDecoratorSerializer<>(
                this,
                toIgnore,
                resourceContext,
                resourceDecorator
        );
    }

    @Override
    protected BeanSerializerBase asArraySerializer() {
        throw new UnsupportedOperationException("asArraySerializer");
    }

    @Override
    public BeanSerializerBase withFilterId(Object filterId) {
        throw new UnsupportedOperationException("withFilterId");
    }

    @Override
    public void serialize(Object bean, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        // Checks the type
        Validate.isTrue(
                resourceDecorator.appliesFor(bean.getClass()),
                format(
                        "The bean class <%s> cannot be processed by the <%s> decorator.",
                        bean.getClass().getName(),
                        resourceDecorator.getClass().getName()
                )
        );
        @SuppressWarnings("unchecked")
        T t = (T) bean;

        // Starting the serialization
        jgen.writeStartObject();

        // Decorating the bean itself before serialization
        T decoratedBean = resourceDecorator.decorateBeforeSerialization(t);

        // Default fields
        serializeFields(decoratedBean, jgen, provider);

        // Decorations
        for (Link link : resourceDecorator.links(decoratedBean, resourceContext)) {
            jgen.writeObjectField(link.getName(), link.getHref());
        }

        // End
        jgen.writeEndObject();
    }

}
