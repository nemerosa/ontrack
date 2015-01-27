package net.nemerosa.ontrack.extension.artifactory.service;

import net.nemerosa.ontrack.client.ClientNotFoundException;
import net.nemerosa.ontrack.client.JsonClient;
import net.nemerosa.ontrack.extension.artifactory.client.ArtifactoryClientImpl;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static net.nemerosa.ontrack.json.JsonUtils.array;
import static net.nemerosa.ontrack.json.JsonUtils.object;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ArtifactoryClientImplTest {

    @Test
    public void buildNumbers() {
        JsonClient jsonClient = mock(JsonClient.class);
        when(jsonClient.get("/api/build/%s", "PROJECT")).thenReturn(
                object()
                        .with("buildsNumbers", array()
                                .with(object().with("uri", "/1").end())
                                .with(object().with("uri", "/2").end())
                                .end())
                        .end()
        );
        ArtifactoryClientImpl client = new ArtifactoryClientImpl(jsonClient);
        assertEquals(
                Arrays.asList("1", "2"),
                client.getBuildNumbers("PROJECT")
        );
    }

    @Test
    public void buildNumbersEmptyForBuildNotFound() {
        JsonClient jsonClient = mock(JsonClient.class);
        when(jsonClient.get("/api/build/%s", "PROJECT")).thenThrow(new ClientNotFoundException("Not found"));
        ArtifactoryClientImpl client = new ArtifactoryClientImpl(jsonClient);
        assertEquals(
                Collections.<String>emptyList(),
                client.getBuildNumbers("PROJECT")
        );
    }

}
