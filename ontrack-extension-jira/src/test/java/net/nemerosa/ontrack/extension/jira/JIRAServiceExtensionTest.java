package net.nemerosa.ontrack.extension.jira;

import net.nemerosa.ontrack.client.ClientNotFoundException;
import net.nemerosa.ontrack.client.JsonClient;
import net.nemerosa.ontrack.extension.jira.client.JIRAClient;
import net.nemerosa.ontrack.extension.jira.client.JIRAClientImpl;
import net.nemerosa.ontrack.extension.jira.model.JIRAIssue;
import net.nemerosa.ontrack.extension.jira.tx.JIRASession;
import net.nemerosa.ontrack.extension.jira.tx.JIRASessionFactory;
import net.nemerosa.ontrack.tx.DefaultTransactionService;
import net.nemerosa.ontrack.tx.TransactionService;
import org.junit.Test;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JIRAServiceExtensionTest {

    @Test
    public void issueNotFound() {
        JIRAConfiguration config = jiraConfiguration();

        JIRASessionFactory jiraSessionFactory = mock(JIRASessionFactory.class);
        TransactionService transactionService = new DefaultTransactionService();
        JIRAConfigurationService jiraConfigurationService = mock(JIRAConfigurationService.class);

        JsonClient jsonClient = mock(JsonClient.class);
        when(jsonClient.get("/rest/api/2/issue/%s?expand=names", "XXX-1")).thenThrow(new ClientNotFoundException("XXX-1"));

        JIRAClient client = new JIRAClientImpl(jsonClient);

        JIRASession session = mock(JIRASession.class);
        when(session.getClient()).thenReturn(client);

        when(jiraSessionFactory.create(config)).thenReturn(session);

        JIRAServiceExtension service = new JIRAServiceExtension(
                new JIRAExtensionFeature(),
                jiraConfigurationService,
                jiraSessionFactory,
                transactionService
        );

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
//
//    @Test
//    public void insertIssueUrlsInMessage() {
//        ExtensionManager extensionManager = mock(ExtensionManager.class);
//        JIRAConfiguration config = new JIRAConfiguration(0, "test", "http://jira", "user",
//                Collections.<String>emptySet(),
//                Sets.newHashSet("TEST-12", "PRJ-12"));
//        JIRASessionFactory jiraSessionFactory = mock(JIRASessionFactory.class);
//        JIRAConfigurationService jiraConfigurationService = mock(JIRAConfigurationService.class);
//        PropertiesService propertiesService = mock(PropertiesService.class);
//        TransactionService transactionService = mock(TransactionService.class);
//        DefaultJIRAService service = new DefaultJIRAService(
//                extensionManager, jiraConfigurationService, propertiesService, transactionService,
//                jiraSessionFactory);
//        String message = service.formatIssuesInMessage(config, "TEST-12, PRJ-12, PRJ-13 List of issues");
//        assertEquals("TEST-12, PRJ-12, <a href=\"http://jira/browse/PRJ-13\">PRJ-13</a> List of issues", message);
//    }
//
//    @Test
//    public void extractIssueKeysFromMessage() {
//        ExtensionManager extensionManager = mock(ExtensionManager.class);
//        JIRAConfiguration config = new JIRAConfiguration(12, "test", "http://jira", "user",
//                Sets.newHashSet("TEST"),
//                Sets.newHashSet("PRJ-12"));
//        JIRAConfigurationService jiraConfigurationService = mock(JIRAConfigurationService.class);
//        JIRASessionFactory jiraSessionFactory = mock(JIRASessionFactory.class);
//        PropertiesService propertiesService = mock(PropertiesService.class);
//        TransactionService transactionService = mock(TransactionService.class);
//
//        when(propertiesService.getPropertyValue(
//                Entity.PROJECT,
//                10,
//                JIRAExtension.EXTENSION,
//                JIRAConfigurationPropertyExtension.NAME
//        )).thenReturn("12");
//        when(jiraConfigurationService.getConfigurationById(12)).thenReturn(config);
//
//        DefaultJIRAService service = new DefaultJIRAService(
//                extensionManager, jiraConfigurationService, propertiesService, transactionService,
//                jiraSessionFactory);
//        Set<String> issues = service.extractIssueKeysFromMessage(10, "TEST-12, PRJ-12, PRJ-13 List of issues");
//        assertEquals(Collections.singleton("PRJ-13"), issues);
//    }
//
//    @Test(expected = NullPointerException.class)
//    public void getLinkForAllIssues_null_config() {
//        DefaultJIRAService service = new DefaultJIRAService(null, null, null, null, null);
//        service.getLinkForAllIssues(null, Collections.<Issue>emptyList());
//
//    }
//
//    @Test(expected = NullPointerException.class)
//    public void getLinkForAllIssues_null_issues() {
//        DefaultJIRAService service = new DefaultJIRAService(null, null, null, null, null);
//        service.getLinkForAllIssues(
//                new JIRAConfiguration(0, "test", "http://jira", "user", Collections.<String>emptySet(), Collections.<String>emptySet()),
//                null);
//
//    }
//
//    @Test
//    public void getLinkForAllIssues_no_issue() {
//        DefaultJIRAService service = new DefaultJIRAService(null, null, null, null, null);
//        String link = service.getLinkForAllIssues(
//                new JIRAConfiguration(0, "test", "http://jira", "user", Collections.<String>emptySet(), Collections.<String>emptySet()),
//                Collections.<Issue>emptyList());
//        assertEquals("The link for no issue is empty", "", link);
//    }
//
//    @Test
//    public void getLinkForAllIssues_one_issue() {
//        Issue issue = mock(Issue.class);
//        when(issue.getKey()).thenReturn("PRJ-13");
//        DefaultJIRAService service = new DefaultJIRAService(null, null, null, null, null);
//        String link = service.getLinkForAllIssues(
//                new JIRAConfiguration(0, "test", "http://jira", "user", Collections.<String>emptySet(), Collections.<String>emptySet()),
//                Collections.singletonList(issue));
//        assertEquals("http://jira/browse/PRJ-13", link);
//    }
//
//    @Test
//    public void getLinkForAllIssues_two_issues() throws UnsupportedEncodingException {
//        Issue issue1 = mock(Issue.class);
//        when(issue1.getKey()).thenReturn("PRJ-13");
//        Issue issue2 = mock(Issue.class);
//        when(issue2.getKey()).thenReturn("PRJ-15");
//        DefaultJIRAService service = new DefaultJIRAService(null, null, null, null, null);
//        String link = service.getLinkForAllIssues(
//                new JIRAConfiguration(0, "test", "http://jira", "user", Collections.<String>emptySet(), Collections.<String>emptySet()),
//                Arrays.asList(issue1, issue2));
//        assertEquals(
//                String.format(
//                        "http://jira/secure/IssueNavigator.jspa?reset=true&mode=hide&jqlQuery=%s",
//                        URLEncoder.encode(
//                                "key in (\"PRJ-13\",\"PRJ-15\")",
//                                "UTF-8")
//                ),
//                link
//        );
//    }

    private JIRAConfiguration jiraConfiguration() {
        return new JIRAConfiguration("test", "http://jira", "user", "secret");
    }

}
