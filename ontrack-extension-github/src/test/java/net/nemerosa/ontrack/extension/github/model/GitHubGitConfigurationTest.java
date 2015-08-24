package net.nemerosa.ontrack.extension.github.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GitHubGitConfigurationTest {

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
