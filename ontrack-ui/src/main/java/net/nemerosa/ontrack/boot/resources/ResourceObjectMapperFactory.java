package net.nemerosa.ontrack.boot.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.ui.resource.JSONResourceModule;
import net.nemerosa.ontrack.ui.resource.ResourceContext;
import net.nemerosa.ontrack.ui.resource.ResourceModule;

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
