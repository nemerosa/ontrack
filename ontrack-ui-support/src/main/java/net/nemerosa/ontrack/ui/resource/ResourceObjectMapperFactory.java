package net.nemerosa.ontrack.ui.resource;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.model.support.JsonViewClass;

import java.io.IOException;
import java.util.List;

public class ResourceObjectMapperFactory {

    private final ObjectMapper objectMapper;

    public ResourceObjectMapperFactory() {
        this(ObjectMapperFactory.create());
    }

    public ResourceObjectMapperFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ResourceObjectMapper resourceObjectMapper(List<ResourceModule> resourceModules, ResourceContext resourceContext) {
        ObjectMapper mapper = this.objectMapper.copy();
        // Registers as JSON modules
        for (ResourceModule resourceModule : resourceModules) {
            mapper = mapper.registerModule(new JSONResourceModule(resourceModule, resourceContext));
        }
        // OK
        return new DefaultResourceObjectMapper(mapper, resourceContext);
    }

    protected class DefaultResourceObjectMapper implements ResourceObjectMapper {

        private final ObjectMapper mapper;
        private final ResourceContext resourceContext;

        public DefaultResourceObjectMapper(ObjectMapper mapper, ResourceContext resourceContext) {
            this.mapper = mapper;
            this.resourceContext = resourceContext;
        }

        @Override
        public ObjectMapper getObjectMapper() {
            return mapper;
        }

        @Override
        public ResourceContext getResourceContext() {
            return resourceContext;
        }

        @Override
        public String write(Object o) throws JsonProcessingException {
            return write(o, JsonViewClass.getViewClass(o));
        }

        @Override
        public void write(JsonGenerator jgen, Object o) throws IOException {
            write(jgen, o, JsonViewClass.getViewClass(o));
        }

        protected ObjectWriter getObjectWriter(Class<?> view) {
            return mapper.writerWithView(view);
        }

        @Override
        public String write(Object o, Class<?> view) throws JsonProcessingException {
            return getObjectWriter(view).writeValueAsString(o);
        }

        @Override
        public void write(JsonGenerator jgen, Object o, Class<?> view) throws IOException {
            // TODO Sets the view in the resource context
            getObjectWriter(view).writeValue(jgen, o);
        }
    }
}
