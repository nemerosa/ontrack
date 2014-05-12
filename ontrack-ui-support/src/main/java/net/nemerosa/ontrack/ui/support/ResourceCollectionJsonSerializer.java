package net.nemerosa.ontrack.ui.support;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceCollection;

import java.io.IOException;
import java.util.Collection;

public class ResourceCollectionJsonSerializer extends JsonSerializer<ResourceCollection<?>> {

    @Override
    public void serialize(ResourceCollection<?> resource, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        ObjectMapper mapper = ObjectMapperFactory.create();
        // Node
        ObjectNode node = mapper.createObjectNode();
        // Self
        node.put("href", resource.getHref().toASCIIString());
        // Links
        Collection<Link> links = resource.getLinks().values();
        for (Link link : links) {
            node.put(
                    link.getName(),
                    JsonUtils.object()
                            .with("href", link.getUri().toASCIIString())
                            .end()
            );
        }
        // Items
        ArrayNode items = mapper.valueToTree(resource.getResources());
        node.put("resources", items);
        // OK
        jgen.writeTree(node);
    }
}
