package net.nemerosa.ontrack.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JsonClientTest {

    @Test
    public void ssl_trust() {
        JsonClient client = new JsonClientImpl(
                OTHttpClientBuilder.create("https://jenkins.nemerosa.net")
                        .withTrustAnyCertificate(true)
                        .build()
        );
        JsonNode node = client.get("/job/nemerosa-ontrack-master-check/api/json");
        assertEquals(
                "nemerosa-ontrack-master-check",
                node.path("displayName").asText()
        );
    }

}
