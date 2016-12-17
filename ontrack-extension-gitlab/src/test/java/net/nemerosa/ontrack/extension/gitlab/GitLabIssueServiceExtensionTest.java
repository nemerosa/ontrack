package net.nemerosa.ontrack.extension.gitlab;

import com.google.common.collect.Sets;
import net.nemerosa.ontrack.extension.git.GitExtensionFeature;
import net.nemerosa.ontrack.extension.gitlab.client.OntrackGitLabClient;
import net.nemerosa.ontrack.extension.gitlab.client.OntrackGitLabClientFactory;
import net.nemerosa.ontrack.extension.gitlab.model.GitLabConfiguration;
import net.nemerosa.ontrack.extension.gitlab.model.GitLabIssueServiceConfiguration;
import net.nemerosa.ontrack.extension.gitlab.model.GitLabIssueWrapper;
import net.nemerosa.ontrack.extension.gitlab.service.GitLabConfigurationService;
import net.nemerosa.ontrack.extension.issues.export.IssueExportServiceFactory;
import net.nemerosa.ontrack.extension.issues.model.Issue;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration;
import net.nemerosa.ontrack.extension.scm.SCMExtensionFeature;
import net.nemerosa.ontrack.model.support.MessageAnnotation;
import net.nemerosa.ontrack.model.support.MessageAnnotator;
import org.gitlab.api.models.GitlabIssue;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GitLabIssueServiceExtensionTest {

    private GitLabIssueServiceExtension extension;
    private GitLabIssueServiceConfiguration configuration;
    private GitLabConfigurationService configurationService;
    private GitLabConfiguration engineConfiguration;
    private OntrackGitLabClientFactory gitHubClientFactory;
    private GitLabIssueWrapper issueWrapper;

    @Before
    public void init() {
        configurationService = mock(GitLabConfigurationService.class);
        gitHubClientFactory = mock(OntrackGitLabClientFactory.class);
        IssueExportServiceFactory issueExportServiceFactory = mock(IssueExportServiceFactory.class);
        extension = new GitLabIssueServiceExtension(
                new GitLabExtensionFeature(new GitExtensionFeature(new SCMExtensionFeature())),
                issueExportServiceFactory,
                configurationService,
                gitHubClientFactory
        );
        engineConfiguration = new GitLabConfiguration(
                "test",
                "url",
                "",
                "",
                false
        );
        configuration = new GitLabIssueServiceConfiguration(
                engineConfiguration,
                "nemerosa/ontrack"
        );
        issueWrapper = new GitLabIssueWrapper(
                new GitlabIssue(),
                "url/xxx",
                "url/xxx"
        );
    }

    @Test
    public void list_of_configurations_is_not_exposed() {
        assertTrue(extension.getConfigurationList().isEmpty());
    }

    @Test
    public void get_configuration_by_name() {
        when(configurationService.getConfiguration("test")).thenReturn(
                engineConfiguration
        );
        IssueServiceConfiguration configuration = extension.getConfigurationByName("test:nemerosa/ontrack");
        assertNotNull(configuration);
        assertEquals("test:nemerosa/ontrack", configuration.getName());
        assertEquals("gitlab", configuration.getServiceId());
    }

    @Test(expected = IllegalStateException.class)
    public void get_configuration_by_name_using_wrong_id() {
        extension.getConfigurationByName("test");
    }

    @Test
    public void message_annotator() {
        MessageAnnotator messageAnnotator = extension.getMessageAnnotator(configuration).orElse(null);
        assertNotNull(messageAnnotator);
        Collection<MessageAnnotation> messageAnnotations = messageAnnotator.annotate("Message for #12");
        assertEquals(2, messageAnnotations.size());
        List<MessageAnnotation> annotationList = new ArrayList<>(messageAnnotations);
        {
            MessageAnnotation annotation = annotationList.get(0);
            assertNull(annotation.getType());
            assertEquals("Message for ", annotation.getText());
            assertTrue(annotation.getAttributes().isEmpty());
        }
        {
            MessageAnnotation annotation = annotationList.get(1);
            assertEquals("a", annotation.getType());
            assertEquals("#12", annotation.getText());
            assertEquals(
                    Collections.singletonMap("href", "url/nemerosa/ontrack/issues/12"),
                    annotation.getAttributes()
            );
        }
    }

    @Test
    public void no_link_for_all_issues() {
        Issue issue = mock(Issue.class);
        String link = extension.getLinkForAllIssues(configuration, Collections.singletonList(issue));
        assertNull(link);
    }

    @Test
    public void get_issue_from_display_key() {
        Issue issue = get_issue_test("#16", 16);
        assertNotNull(issue);
        assertEquals(issueWrapper, issue);
    }

    @Test
    public void get_issue_from_key() {
        Issue issue = get_issue_test("16", 16);
        assertNotNull(issue);
        assertEquals(issueWrapper, issue);
    }

    @Test
    public void get_issue_not_found() {
        assertNull(get_issue_test("18", 0));
    }

    protected Issue get_issue_test(String token, int id) {
        OntrackGitLabClient client = mock(OntrackGitLabClient.class);
        when(client.getIssue(configuration.getRepository(), id)).thenReturn(issueWrapper);
        when(gitHubClientFactory.create(configuration.getConfiguration())).thenReturn(client);
        return extension.getIssue(configuration, token);
    }

    @Test
    public void issueServiceIdentifierContainsBothConfigurationAndRepository() {
        when(configurationService.getConfiguration("Test")).thenReturn(
                new GitLabConfiguration(
                        "Test",
                        "https://gitlab.test.com",
                        null,
                        null,
                        false
                )
        );
        IssueServiceConfiguration configuration = extension.getConfigurationByName("Test:nemerosa/ontrack");
        assertEquals("gitlab", configuration.getServiceId());
        assertEquals("Test:nemerosa/ontrack", configuration.getName());
        assertTrue(configuration instanceof GitLabIssueServiceConfiguration);
        GitLabIssueServiceConfiguration issueServiceConfiguration = (GitLabIssueServiceConfiguration) configuration;
        assertEquals("Test", issueServiceConfiguration.getConfiguration().getName());
        assertEquals("https://gitlab.test.com", issueServiceConfiguration.getConfiguration().getUrl());
        assertEquals("nemerosa/ontrack", issueServiceConfiguration.getRepository());
    }

    @Test
    public void getIssueId_full() {
        Optional<String> o = extension.getIssueId(configuration, "#12");
        assertTrue(o.isPresent());
        assertEquals("12", o.orElse(null));
    }

    @Test
    public void getIssueId_numeric() {
        Optional<String> o = extension.getIssueId(configuration, "12");
        assertTrue(o.isPresent());
        assertEquals("12", o.orElse(null));
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