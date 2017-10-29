package net.nemerosa.ontrack.extension.github.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import static net.nemerosa.ontrack.json.JsonUtils.object;
import static net.nemerosa.ontrack.test.TestUtils.assertJsonRead;
import static net.nemerosa.ontrack.test.TestUtils.assertJsonWrite;
import static org.junit.Assert.assertEquals;

public class GitHubEngineConfigurationTest {

    @Test
    public void obfuscation_of_password() {
        GitHubEngineConfiguration configuration = new GitHubEngineConfiguration(
                "ontrack",
                GitHubEngineConfiguration.GITHUB_COM,
                "user",
                "secret",
                null
        );
        assertEquals("", configuration.obfuscate().getPassword());
    }

    @Test
    public void obfuscation_of_token() {
        GitHubEngineConfiguration configuration = new GitHubEngineConfiguration(
                "ontrack",
                GitHubEngineConfiguration.GITHUB_COM,
                null,
                null,
                "1234567890abcdef"
        );
        assertEquals("", configuration.obfuscate().getOauth2Token());
    }

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

    @Test
    public void null_url_for_github_com() {
        GitHubEngineConfiguration configuration = new GitHubEngineConfiguration(
                "Test",
                null,
                "",
                "",
                ""
        );
        assertEquals("https://github.com", configuration.getUrl());
    }

    @Test
    public void empty_url_for_github_com() {
        GitHubEngineConfiguration configuration = new GitHubEngineConfiguration(
                "Test",
                "",
                "",
                "",
                ""
        );
        assertEquals("https://github.com", configuration.getUrl());
    }

}