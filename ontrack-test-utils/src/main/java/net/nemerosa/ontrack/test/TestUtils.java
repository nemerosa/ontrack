package net.nemerosa.ontrack.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.net.ServerSocket;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertEquals;

public final class TestUtils {

    private static final ObjectMapper mapper = ObjectMapperFactory.create();
    private static final AtomicLong counter = new AtomicLong();

    private TestUtils() {
    }

    public static List<Integer> range(int from, int to) {
        Validate.isTrue(to >= from, "'to' value must be greater or equal to the 'from' value.");
        List<Integer> l = new ArrayList<>();
        for (int i = from; i <= to; i++) {
            l.add(i);
        }
        return l;
    }

    public static String uid(String prefix) {
        return prefix + new SimpleDateFormat("mmssSSS").format(new Date()) + counter.incrementAndGet();
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

    public static void assertJsonWrite(JsonNode expectedJson, Object objectToWrite, Class<?> viewClass) throws JsonProcessingException {
        assertJsonWrite(
                mapper,
                expectedJson,
                objectToWrite,
                viewClass
        );
    }

    public static void assertJsonWrite(ObjectMapper mapper, JsonNode expectedJson, Object objectToWrite, Class<?> viewClass) throws JsonProcessingException {
        assertEquals(
                mapper.writeValueAsString(expectedJson),
                mapper.writerWithView(viewClass).writeValueAsString(objectToWrite)
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

    public static LocalDateTime dateTime() {
        return LocalDateTime.of(2014, 5, 13, 20, 11);
    }

    public static String dateTimeJson() {
        return "2014-05-13T20:11:00Z";
    }

    public static byte[] resourceBytes(String path) throws IOException {
        return IOUtils.toByteArray(TestUtils.class.getResource(path));
    }

    public static JsonNode resourceJson(String path) throws IOException {
        return mapper.readTree(resourceBytes(path));
    }

    public static int getAvailablePort() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            return serverSocket.getLocalPort();
        }
    }
}
