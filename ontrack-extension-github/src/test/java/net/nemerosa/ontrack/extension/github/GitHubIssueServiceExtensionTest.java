package net.nemerosa.ontrack.extension.github;

import com.google.common.collect.Sets;
import net.nemerosa.ontrack.extension.github.client.GitHubClientConfiguratorFactory;
import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClient;
import net.nemerosa.ontrack.extension.github.model.GitHubConfiguration;
import net.nemerosa.ontrack.extension.github.service.GitHubConfigurationService;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class GitHubIssueServiceExtensionTest {

    private GitHubIssueServiceExtension extension;
    private GitHubConfiguration configuration;

    @Before
    public void init() {
        GitHubConfigurationService configurationService = mock(GitHubConfigurationService.class);
        GitHubClientConfiguratorFactory gitHubClientConfiguratorFactory = mock(GitHubClientConfiguratorFactory.class);
        OntrackGitHubClient gitHubClient = mock(OntrackGitHubClient.class);
        extension = new GitHubIssueServiceExtension(
                new GitHubExtensionFeature(),
                configurationService,
                gitHubClientConfiguratorFactory,
                gitHubClient
        );

        configuration = new GitHubConfiguration(
                "test",
                "repo/test",
                "",
                "",
                "",
                0
        );
    }

    @Test
    public void extractIssueKeysFromMessage_none() {
        Set<String> keys = extension.extractIssueKeysFromMessage(configuration, "TEST-1 No GitHub issue");
        assertTrue(keys.isEmpty());
    }

    @Test
    public void extractIssueKeysFromMessage_one() {
        Set<String> keys = extension.extractIssueKeysFromMessage(configuration, "#12 One GitHub issue");
        assertEquals(
                Sets.newHashSet("12"),
                keys
        );
    }

    @Test
    public void extractIssueKeysFromMessage_two() {
        Set<String> keys = extension.extractIssueKeysFromMessage(configuration, "#12 Two GitHub #45 issue");
        assertEquals(
                Sets.newHashSet("12", "45"),
                keys
        );
    }


}