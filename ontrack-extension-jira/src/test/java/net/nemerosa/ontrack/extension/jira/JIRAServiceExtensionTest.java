package net.nemerosa.ontrack.extension.jira;

import com.google.common.collect.Sets;
import net.nemerosa.ontrack.client.ClientNotFoundException;
import net.nemerosa.ontrack.client.JsonClient;
import net.nemerosa.ontrack.extension.issues.model.Issue;
import net.nemerosa.ontrack.extension.jira.client.JIRAClient;
import net.nemerosa.ontrack.extension.jira.client.JIRAClientImpl;
import net.nemerosa.ontrack.extension.jira.model.JIRAIssue;
import net.nemerosa.ontrack.extension.jira.tx.JIRASession;
import net.nemerosa.ontrack.extension.jira.tx.JIRASessionFactory;
import net.nemerosa.ontrack.model.support.MessageAnnotationUtils;
import net.nemerosa.ontrack.model.support.MessageAnnotator;
import net.nemerosa.ontrack.tx.DefaultTransactionService;
import net.nemerosa.ontrack.tx.TransactionService;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JIRAServiceExtensionTest {

    private JIRASessionFactory jiraSessionFactory;
    private JsonClient jsonClient;
    private JIRASession session;
    private JIRAServiceExtension service;

    @Before
    public void before() {
        jiraSessionFactory = mock(JIRASessionFactory.class);
        TransactionService transactionService = new DefaultTransactionService();
        JIRAConfigurationService jiraConfigurationService = mock(JIRAConfigurationService.class);

        jsonClient = mock(JsonClient.class);
        JIRAClient client = new JIRAClientImpl(jsonClient);

        session = mock(JIRASession.class);
        when(session.getClient()).thenReturn(client);

        service = new JIRAServiceExtension(
                new JIRAExtensionFeature(),
                jiraConfigurationService,
                jiraSessionFactory,
                transactionService
        );
    }

    @Test
    public void issueNotFound() {
        JIRAConfiguration config = jiraConfiguration();

        when(jsonClient.get("/rest/api/2/issue/%s?expand=names", "XXX-1")).thenThrow(new ClientNotFoundException("XXX-1"));
        when(jiraSessionFactory.create(config)).thenReturn(session);

        JIRAIssue issue = service.getIssue(config, "XXX-1");
        assertNull(issue);
    }

//    @Test
//    public void isIssue() {
//        ExtensionManager extensionManager = mock(ExtensionManager.class);
//        JIRAConfiguration config = jiraConfiguration();
//        JIRASessionFactory jiraSessionFactory = mock(JIRASessionFactory.class);
//        JIRAConfigurationService jiraConfigurationService = mock(JIRAConfigurationService.class);
//        PropertiesService propertiesService = mock(PropertiesService.class);
//        TransactionService transactionService = mock(TransactionService.class);
//
//        DefaultJIRAService service = new DefaultJIRAService(
//                extensionManager, jiraConfigurationService, propertiesService, transactionService,
//                jiraSessionFactory);
//        assertTrue(service.isIssue(config, "TEST-12"));
//    }

    @Test
    public void getMessageAnnotator() {
        JIRAConfiguration config = jiraConfiguration();
        Optional<MessageAnnotator> annotator = service.getMessageAnnotator(config);
        assertTrue(annotator.isPresent());
        String message = MessageAnnotationUtils.annotate("TEST-12, PRJ-12, PRJ-13 List of issues", Collections.singletonList(annotator.get()));
        // TODO List of excluded projects and issues
        assertEquals("<a href=\"http://jira/browse/TEST-12\">TEST-12</a>, <a href=\"http://jira/browse/PRJ-12\">PRJ-12</a>, <a href=\"http://jira/browse/PRJ-13\">PRJ-13</a> List of issues", message);
    }

    @Test
    public void extractIssueKeysFromMessage() {
        JIRAConfiguration config = jiraConfiguration();

        // TODO List of excluded projects and issues
        Set<String> issues = service.extractIssueKeysFromMessage(config, "TEST-12, PRJ-12, PRJ-13 List of issues");
        assertEquals(Sets.newHashSet("TEST-12", "PRJ-12", "PRJ-13"), issues);
    }

    @Test(expected = NullPointerException.class)
    public void getLinkForAllIssues_null_config() {
        service.getLinkForAllIssues(null, Collections.emptyList());
    }

    @Test(expected = NullPointerException.class)
    public void getLinkForAllIssues_null_issues() {
        service.getLinkForAllIssues(
                jiraConfiguration(),
                null);
    }

    @Test
    public void getLinkForAllIssues_no_issue() {
        String link = service.getLinkForAllIssues(
                jiraConfiguration(),
                Collections.emptyList());
        assertEquals("The link for no issue is empty", "", link);
    }

    @Test
    public void getLinkForAllIssues_one_issue() {
        Issue issue = mock(Issue.class);
        when(issue.getKey()).thenReturn("PRJ-13");
        String link = service.getLinkForAllIssues(
                jiraConfiguration(),
                Collections.singletonList(issue));
        assertEquals("http://jira/browse/PRJ-13", link);
    }

    @Test
    public void getLinkForAllIssues_two_issues() throws UnsupportedEncodingException {
        Issue issue1 = mock(Issue.class);
        when(issue1.getKey()).thenReturn("PRJ-13");
        Issue issue2 = mock(Issue.class);
        when(issue2.getKey()).thenReturn("PRJ-15");
        String link = service.getLinkForAllIssues(
                jiraConfiguration(),
                Arrays.asList(issue1, issue2));
        assertEquals(
                String.format(
                        "http://jira/secure/IssueNavigator.jspa?reset=true&mode=hide&jqlQuery=%s",
                        URLEncoder.encode(
                                "key in (\"PRJ-13\",\"PRJ-15\")",
                                "UTF-8")
                ),
                link
        );
    }

    private JIRAConfiguration jiraConfiguration() {
        return new JIRAConfiguration("test", "http://jira", "user", "secret");
    }

}
