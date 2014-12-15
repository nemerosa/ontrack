package net.nemerosa.ontrack.extension.github;

import com.google.common.collect.Sets;
import net.nemerosa.ontrack.extension.github.client.GitHubClientConfiguratorFactory;
import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClient;
import net.nemerosa.ontrack.extension.github.model.GitHubConfiguration;
import net.nemerosa.ontrack.extension.github.service.GitHubConfigurationService;
import net.nemerosa.ontrack.extension.issues.export.IssueExportServiceFactory;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class GitHubIssueServiceExtensionTest {

    private GitHubIssueServiceExtension extension;
    private GitHubConfiguration configuration;

    @Before
    public void init() {
        GitHubConfigurationService configurationService = mock(GitHubConfigurationService.class);
        GitHubClientConfiguratorFactory gitHubClientConfiguratorFactory = mock(GitHubClientConfiguratorFactory.class);
        OntrackGitHubClient gitHubClient = mock(OntrackGitHubClient.class);
        IssueExportServiceFactory issueExportServiceFactory = mock(IssueExportServiceFactory.class);
        extension = new GitHubIssueServiceExtension(
                new GitHubExtensionFeature(),
                configurationService,
                gitHubClientConfiguratorFactory,
                gitHubClient,
                issueExportServiceFactory
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
    public void getIssueId_full() {
        Optional<String> o = extension.getIssueId(configuration, "#12");
        assertTrue(o.isPresent());
        assertEquals("12", o.get());
    }

    @Test
    public void getIssueId_numeric() {
        Optional<String> o = extension.getIssueId(configuration, "12");
        assertTrue(o.isPresent());
        assertEquals("12", o.get());
    }

    @Test
    public void getIssueId_not_valid() {
        Optional<String> o = extension.getIssueId(configuration, "mm");
        assertFalse(o.isPresent());
    }

    @Test
    public void containsIssueKey_one_in_none() {
        assertFalse(extension.containsIssueKey(configuration, "12", Collections.emptySet()));
    }

    @Test
    public void containsIssueKey_one() {
        assertTrue(extension.containsIssueKey(configuration, "12", Sets.newHashSet("12")));
    }

    @Test
    public void containsIssueKey_none_in_one() {
        assertFalse(extension.containsIssueKey(configuration, "8", Sets.newHashSet("12")));
    }

    @Test
    public void containsIssueKey_one_in_two() {
        assertTrue(extension.containsIssueKey(configuration, "12", Sets.newHashSet("8", "12")));
    }

    @Test
    public void containsIssueKey_none_in_two() {
        assertFalse(extension.containsIssueKey(configuration, "24", Sets.newHashSet("8", "12")));
    }

    @Test
    public void containsIssueKey_jira_in_none() {
        assertFalse(extension.containsIssueKey(configuration, "ITEACH-14", Collections.emptySet()));
    }

    @Test
    public void containsIssueKey_jira_in_one() {
        assertFalse(extension.containsIssueKey(configuration, "ITEACH-14", Sets.newHashSet("15")));
    }

    @Test
    public void containsIssueKey_jira_in_two() {
        assertFalse(extension.containsIssueKey(configuration, "ITEACH-14", Sets.newHashSet("15", "22")));
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

    @Test
    public void getIssueId_no_prefix() {
        assertEquals(14, extension.getIssueId("14"));
    }

    @Test
    public void getIssueId_with_prefix() {
        assertEquals(14, extension.getIssueId("#14"));
    }


}