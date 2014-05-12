package net.nemerosa.ontrack.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public final class TestUtils {

    private static final ObjectMapper mapper = ObjectMapperFactory.create();

    private TestUtils() {
    }

    public static String uid(String prefix) {
        return prefix + new SimpleDateFormat("mmssSSS").format(new Date());
    }

    public static String getEnvIfPresent(String systemProperty, String envProperty, String defaulValue) {
        // Trying with the system property first
        String value = System.getProperty(systemProperty);
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        // Trying with the environment variable
        value = System.getenv(envProperty);
        if (StringUtils.isNotBlank(value)) {
            return value;
        }
        // Not found
        return defaulValue;
    }

    public static String getEnv(String systemProperty, String envProperty, String name) {
        String value = getEnvIfPresent(systemProperty, envProperty, null);
        if (StringUtils.isBlank(value)) {
            throw new IllegalStateException(
                    String.format(
                            "The %s value must be defined with the system property `%s` or the environment variable `%s`.",
                            name,
                            systemProperty,
                            envProperty
                    )
            );
        } else {
            return value;
        }
    }

    public static void assertJsonWrite(JsonNode expectedJson, Object objectToWrite) throws JsonProcessingException {
        assertEquals(
                mapper.writeValueAsString(expectedJson),
                mapper.writeValueAsString(objectToWrite)
        );
    }

    public static <T> void assertJsonRead(T expectedResult, JsonNode jsonToRead, Class<T> type) throws JsonProcessingException {
        assertEquals(
                expectedResult,
                mapper.treeToValue(jsonToRead, type)
        );
    }
    
    public static void assertJsonEquals(TreeNode o1, TreeNode o2) throws JsonProcessingException {
        assertEquals(
                mapper.writeValueAsString(o1),
                mapper.writeValueAsString(o2)
        );
    }
}
