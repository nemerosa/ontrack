package net.nemerosa.ontrack.boot.resource;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.json.ObjectMapperFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class ResourceJsonSerializer extends JsonSerializer<Resource<?>> {

    private final ObjectMapper mapper = ObjectMapperFactory.create();

    @Override
    public void serialize(Resource<?> resource, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        ObjectNode data = toJson(resource);
        jgen.writeTree(data);
    }

    private ObjectNode toJson(Resource<?> resource) {
        Object data = resource.getData();
        // Root node
        ObjectNode node;
        if (data instanceof Collection) {
            node = mapper.createObjectNode();
            node.put("collection", mapper.valueToTree(data));
        } else {
            node = mapper.valueToTree(data);
        }
        // Links
        Map<String, Link<?>> links = resource.getLinks();
        for (Map.Entry<String, Link<?>> linkEntry : links.entrySet()) {
            addLink(node, linkEntry.getKey(), linkEntry.getValue());
        }
        return node;
    }

    protected <T> void addLink(ObjectNode data, String rel, Link<T> link) {
        Resource<T> linkedResource = link.getResource();
        if (linkedResource != null) {
            ObjectNode linkedNode = toJson(linkedResource);
            // Adds the node
            data.put(rel, linkedNode);
        } else if (Resource.SELF.equals(rel)) {
            data.put(Resource.SELF, link.getUri());
        } else {
            data.put(
                    rel,
                    JsonUtils.object()
                            .with("href", link.getUri())
                            .end()
            );
        }
    }

}
