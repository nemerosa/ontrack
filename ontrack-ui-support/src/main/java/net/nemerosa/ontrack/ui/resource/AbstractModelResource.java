package net.nemerosa.ontrack.ui.resource;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.impl.ObjectIdWriter;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import net.nemerosa.ontrack.ui.controller.URIBuilder;

import java.io.IOException;
import java.net.URI;

public abstract class AbstractModelResource<T> extends BeanSerializerBase {

    private final URIBuilder uriBuilder;

    protected AbstractModelResource(BeanSerializerBase src, URIBuilder uriBuilder) {
        super(src);
        this.uriBuilder = uriBuilder;
    }

    @Override
    public BeanSerializerBase withObjectIdWriter(ObjectIdWriter objectIdWriter) {
        throw new UnsupportedOperationException("withObjectIdWriter");
    }

    @Override
    protected BeanSerializerBase withIgnorals(String[] toIgnore) {
        throw new UnsupportedOperationException("withIgnorals");
    }

    @Override
    protected BeanSerializerBase asArraySerializer() {
        throw new UnsupportedOperationException("asArraySerializer");
    }

    @Override
    protected BeanSerializerBase withFilterId(Object filterId) {
        throw new UnsupportedOperationException("withFilterId");
    }

    @Override
    public void serialize(Object bean, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        serializeFields(bean, jgen, provider);
        additionalFields((T) bean, jgen, provider);
        jgen.writeEndObject();
    }

    protected abstract void additionalFields(T bean, JsonGenerator jgen, SerializerProvider provider) throws IOException;

    /**
     * @see org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder#fromMethodCall(Object)
     */
    protected URI uri(Object methodInvocation) {
        return uriBuilder.build(methodInvocation);
    }

}
