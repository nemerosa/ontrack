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

    public ObjectMapper resourceObjectMapper(List<ResourceModule> resourceModules, ResourceContext resourceContext) {
        ObjectMapper mapper = this.objectMapper.copy();
        // Registers as JSON modules
        for (ResourceModule resourceModule : resourceModules) {
            mapper = mapper.registerModule(new JSONResourceModule(resourceModule, resourceContext));
        }
        // OK
        return mapper;
    }
}
