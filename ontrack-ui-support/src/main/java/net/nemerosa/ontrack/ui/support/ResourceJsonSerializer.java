package net.nemerosa.ontrack.ui.support;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializer;
import com.fasterxml.jackson.databind.ser.BeanSerializerBuilder;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import com.fasterxml.jackson.databind.type.TypeFactory;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.Resource;

import java.io.IOException;
import java.util.Collection;

@Deprecated
public class ResourceJsonSerializer extends JsonSerializer<Resource<?>> {

    @Override
    public void serialize(Resource<?> resource, JsonGenerator jgen, SerializerProvider provider) throws IOException {

        ObjectCodec codec = jgen.getCodec();
        TreeNode node = codec.createObjectNode();
        jgen.writeTree(node);

        /*
        ObjectMapper mapper = ObjectMapperFactory.create();
        // Data
        ObjectNode data = mapper.valueToTree(resourceData);
        // Self
        data.put("href", resource.getHref().toASCIIString());
        // Links
        Collection<Link> links = resource.getLinks().values();
        for (Link link : links) {
            data.put(
                    link.getName(),
                    JsonUtils.object()
                            .with("href", link.getHref().toASCIIString())
                            .end()
            );
        }
        // OK
        jgen.writeTree(data);
        */
    }
}
