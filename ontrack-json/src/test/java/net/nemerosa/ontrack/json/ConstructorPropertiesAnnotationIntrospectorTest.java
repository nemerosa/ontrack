package net.nemerosa.ontrack.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ConstructorPropertiesAnnotationIntrospectorTest {

    private static class ImmutablePojo {
        private final String name;
        private final int value;

        private ImmutablePojo(String name, int value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }
    }

    private final ImmutablePojo instance = new ImmutablePojo("foobar", 42);

    @Test
    public void testJacksonAbleToDeserialize() throws IOException {
        ObjectMapper mapper = ObjectMapperFactory.create();
        String json = mapper.writeValueAsString(instance);
        ImmutablePojo output = mapper.readValue(json, ImmutablePojo.class);
        assertThat(output, equalTo(instance));
    }
}
