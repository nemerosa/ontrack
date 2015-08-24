package net.nemerosa.ontrack.extension.github.model;

import net.nemerosa.ontrack.extension.github.property.GitHubGitConfiguration;
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationProperty;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GitHubGitConfigurationTest {

    @Test
    public void issueServiceIdentifierContainsBothConfigurationAndRepository() {
        GitHubGitConfiguration configuration = new GitHubGitConfiguration(
                new GitHubProjectConfigurationProperty(
                        new GitHubEngineConfiguration(
                                "Test",
                                null,
                                null,
                                null,
                                null
                        ),
                        "nemerosa/ontrack",
                        30
                )
        );
        assertEquals("github//Test:nemerosa/ontrack", configuration.getIssueServiceConfigurationIdentifier());
    }

}
