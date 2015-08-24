package net.nemerosa.ontrack.extension.github.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import static net.nemerosa.ontrack.json.JsonUtils.object;
import static net.nemerosa.ontrack.test.TestUtils.assertJsonRead;
import static net.nemerosa.ontrack.test.TestUtils.assertJsonWrite;

public class GitHubEngineConfigurationTest {

    @Test
    public void toJson() throws JsonProcessingException {
        assertJsonWrite(
                object()
                        .with("name", "ontrack")
                        .with("url", "https://github.com")
                        .withNull("user")
                        .withNull("password")
                        .with("oauth2Token", "1234567890abcdef")
                        .end(),
                new GitHubEngineConfiguration(
                        "ontrack",
                        GitHubEngineConfiguration.GITHUB_COM,
                        null,
                        null,
                        "1234567890abcdef"
                )
        );
    }

    @Test
    public void fromJson() throws JsonProcessingException {
        assertJsonRead(
                new GitHubEngineConfiguration(
                        "ontrack",
                        GitHubEngineConfiguration.GITHUB_COM,
                        null,
                        null,
                        "1234567890abcdef"
                ),
                object()
                        .with("name", "ontrack")
                        .with("url", "https://github.com")
                        .withNull("user")
                        .withNull("password")
                        .with("oauth2Token", "1234567890abcdef")
                        .end(),
                GitHubEngineConfiguration.class
        );
    }

}