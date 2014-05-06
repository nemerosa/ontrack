package net.nemerosa.ontrack.json;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class ConstructorPropertiesAnnotationIntrospectorTest {

    @Data
    private static class ImmutablePojo {
        private final String name;
        private final int value;
    }

    private final ImmutablePojo instance = new ImmutablePojo("foobar", 42);

    @Test(expected = JsonMappingException.class)
    public void testJacksonUnableToDeserialize() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(instance);
        mapper.readValue(json, ImmutablePojo.class);
    }

    @Test
    public void testJacksonAbleToDeserialize() throws IOException {
        ObjectMapper mapper = ObjectMapperFactory.create();
        String json = mapper.writeValueAsString(instance);
        ImmutablePojo output = mapper.readValue(json, ImmutablePojo.class);
        assertThat(output, equalTo(instance));
    }
}
