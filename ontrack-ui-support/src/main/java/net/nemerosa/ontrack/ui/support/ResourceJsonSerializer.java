package net.nemerosa.ontrack.ui.support;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.Resource;

import java.io.IOException;
import java.util.Collection;

public class ResourceJsonSerializer extends JsonSerializer<Resource<?>> {

    @Override
    public void serialize(Resource<?> resource, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        ObjectMapper mapper = ObjectMapperFactory.create();
        // Data
        ObjectNode data = mapper.valueToTree(resource.getData());
        // Self
        data.put("href", resource.getHref().toASCIIString());
        // Links
        Collection<Link> links = resource.getLinks().values();
        for (Link link : links) {
            data.put(
                    link.getName(),
                    JsonUtils.object()
                            .with("href", link.getUri().toASCIIString())
                            .end()
            );
        }
        // OK
        jgen.writeTree(data);
    }
}
