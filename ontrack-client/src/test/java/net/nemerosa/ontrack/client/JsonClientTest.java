package net.nemerosa.ontrack.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class JsonClientTest {

    @Test
    public void ssl_trust() throws IOException {
        JsonClient client = new JsonClientImpl(
                OTHttpClientBuilder.create("https://jenkins.nemerosa.net").build()
        );
        JsonNode node = client.get("/job/nemerosa-ontrack-master-check/api/json");
        assertEquals(
                "nemerosa-ontrack-master-check",
                node.path("displayName").asText()
        );
    }

}
