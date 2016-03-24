package net.nemerosa.ontrack.model.support;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.model.structure.NameDescription;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ApplicationLogEntryTest {

    @Test
    public void details() {
        ApplicationLogEntry entry = ApplicationLogEntry.error(
                new RuntimeException("error"),
                NameDescription.nd("test", "Test"),
                "Test error")
                .withDetail("a", "A")
                .withDetail("b", "B")
                .withAuthentication("admin");
        assertEquals(ApplicationLogEntryLevel.ERROR, entry.getLevel());
        assertEquals(NameDescription.nd("test", "Test"), entry.getType());
        assertEquals("admin", entry.getAuthentication());
        assertEquals("Test error", entry.getInformation());
        assertEquals(Arrays.asList(
                NameDescription.nd("a", "A"),
                NameDescription.nd("b", "B")
        ), entry.getDetailList());
    }

    /**
     * Serialisation of a log entry with a nested exception (#412)
     */
    @Test
    public void json_with_nested_exception() throws IOException {
        IOException root = new IOException("IO error");
        ApplicationLogEntry entry = ApplicationLogEntry.error(
                // Nested exception
                new RuntimeException("error", root),
                NameDescription.nd("test", "Test"),
                "Test error")
                .withDetail("a", "A")
                .withDetail("b", "B")
                .withAuthentication("admin");
        ObjectMapper mapper = ObjectMapperFactory.create();
        JsonNode node = mapper.valueToTree(entry);
        assertNull("The `exception` field must not be serialised to JSON", node.get("exception"));
        assertNull("The `details` field must not be serialised to JSON", node.get("details"));
    }

}
