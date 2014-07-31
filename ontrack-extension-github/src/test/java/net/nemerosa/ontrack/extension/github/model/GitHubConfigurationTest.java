package net.nemerosa.ontrack.extension.github.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import static net.nemerosa.ontrack.json.JsonUtils.object;
import static net.nemerosa.ontrack.test.TestUtils.assertJsonRead;
import static net.nemerosa.ontrack.test.TestUtils.assertJsonWrite;

public class GitHubConfigurationTest {

    @Test
    public void toJson() throws JsonProcessingException {
        assertJsonWrite(
                object()
                        .with("name", "ontrack")
                        .with("repository", "nemerosa/ontrack")
                        .withNull("user")
                        .withNull("password")
                        .with("oauth2Token", "1234567890abcdef")
                        .with("indexationInterval", 60)
                        .end(),
                new GitHubConfiguration(
                        "ontrack",
                        "nemerosa/ontrack",
                        null,
                        null,
                        "1234567890abcdef",
                        60
                )
        );
    }

    @Test
    public void fromJson() throws JsonProcessingException {
        assertJsonRead(
                new GitHubConfiguration(
                        "ontrack",
                        "nemerosa/ontrack",
                        null,
                        null,
                        "1234567890abcdef",
                        60
                ),
                object()
                        .with("name", "ontrack")
                        .with("repository", "nemerosa/ontrack")
                        .withNull("user")
                        .withNull("password")
                        .with("indexationInterval", 60)
                        .with("oauth2Token", "1234567890abcdef")
                        .with("serviceId", "ontrack") // This field is ignored
                        .end(),
                GitHubConfiguration.class
        );
    }

}