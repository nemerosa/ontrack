package net.nemerosa.ontrack.ui.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.nemerosa.ontrack.json.ObjectMapperFactory;

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
    }
}
