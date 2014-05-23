package net.nemerosa.ontrack.model.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.model.structure.Signature;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;

public class TimeTest {

    @Test
    public void end_to_end() throws JsonProcessingException {
        // Server time
        LocalDateTime time = Time.now();
        System.out.format("Server time: %s%n", time);
        // For storage
        String stored = Time.forStorage(time);
        System.out.format("Stored time: %s%n", stored);
        // From storage
        LocalDateTime retrieved = Time.fromStorage(stored);
        System.out.format("Retrieved time: %s%n", retrieved);
        // Out for JSON
        Signature signature = Signature.of(time, "user");
        JsonNode jsonNode = ObjectMapperFactory.create().valueToTree(signature);
        String json = ObjectMapperFactory.create().writeValueAsString(jsonNode);
        System.out.format("JSON output: %s%n", json);
        // Extracts the date from the JSON
        String jsonTime = jsonNode.path("time").asText();
        System.out.format("JSON time: %s%n", jsonTime);
        // Converts to a LocalDateTime
        LocalDateTime parsed = LocalDateTime.parse(jsonTime, DateTimeFormatter.ISO_DATE_TIME);
        System.out.format("Parsed time: %s%n", parsed);
        // Checks equality
        assertEquals("The initial date & the parsed date must be equal", time, parsed);
    }

}
