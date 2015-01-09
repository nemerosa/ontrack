package net.nemerosa.ontrack.extension.jira

import com.google.common.collect.Sets
import net.nemerosa.ontrack.client.ClientNotFoundException
import net.nemerosa.ontrack.client.JsonClient
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.issues.export.IssueExportServiceFactory
import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.jira.client.JIRAClient
import net.nemerosa.ontrack.extension.jira.client.JIRAClientImpl
import net.nemerosa.ontrack.extension.jira.model.JIRAIssue
import net.nemerosa.ontrack.extension.jira.model.JIRALink
import net.nemerosa.ontrack.extension.jira.model.JIRAStatus
import net.nemerosa.ontrack.extension.jira.tx.JIRASession
import net.nemerosa.ontrack.extension.jira.tx.JIRASessionFactory
import net.nemerosa.ontrack.model.support.MessageAnnotationUtils
import net.nemerosa.ontrack.model.support.MessageAnnotator
import net.nemerosa.ontrack.tx.DefaultTransactionService
import net.nemerosa.ontrack.tx.TransactionService
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.*
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

public class JIRAServiceExtensionTest {

    private JIRASessionFactory jiraSessionFactory;
    private JIRAClient client
    private JIRASession session;
    private JIRAServiceExtension service;

    @Before
    public void before() {
        jiraSessionFactory = mock(JIRASessionFactory.class);
        TransactionService transactionService = new DefaultTransactionService();
        JIRAConfigurationService jiraConfigurationService = mock(JIRAConfigurationService.class);

        client = mock(JIRAClient)

        session = mock(JIRASession.class);
        when(session.getClient()).thenReturn(client);

        IssueExportServiceFactory issueExportServiceFactory = mock(IssueExportServiceFactory.class);

        service = new JIRAServiceExtension(
                new JIRAExtensionFeature(),
                jiraConfigurationService,
                jiraSessionFactory,
                transactionService,
                issueExportServiceFactory
        );
    }

    @Test
    public void issueNotFound() {
        JIRAConfiguration config = jiraConfiguration();

        JsonClient jsonClient = mock(JsonClient)
        when(jsonClient.get("/rest/api/2/issue/%s?expand=names", "XXX-1")).thenThrow(new ClientNotFoundException("XXX-1"));
        client = new JIRAClientImpl(jsonClient)
        when(session.getClient()).thenReturn(client)
        when(jiraSessionFactory.create(config)).thenReturn(session)

        JIRAIssue issue = service.getIssue(config, "XXX-1");
        assertNull(issue);
    }

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

    @Test
    void 'Following links'() {
        // Configuration to test with
        JIRAConfiguration config = jiraConfiguration()
        when(jiraSessionFactory.create(config)).thenReturn(session)
        // Creating issues
        JIRAIssue issue1 = createIssue(1)
        JIRAIssue issue2 = createIssue(2)
        JIRAIssue issue3 = createIssue(3)
        JIRAIssue issue4 = createIssue(4)
        // Linking issues together
        issue1 = issue1.withLinks([
                createLink(2, "Depends", "depends on"),
                createLink(3, "Depends", "depends on"),
        ])
        issue2 = issue2.withLinks([
                createLink(1, "Depends", "is depended on by"),
                createLink(4, "Depends", "depends on"),
        ])
        issue3 = issue3.withLinks([
                createLink(1, "Depends", "is depended on by"),
        ])
        issue4 = issue4.withLinks([
                createLink(2, "Depends", "is depended on by"),
        ])

        // Client
        when(client.getIssue('TEST-1', config)).thenReturn(issue1)
        when(client.getIssue('TEST-2', config)).thenReturn(issue2)
        when(client.getIssue('TEST-3', config)).thenReturn(issue3)
        when(client.getIssue('TEST-4', config)).thenReturn(issue4)

        // Links from 1
        def issues = [:]
        service.followLinks(config, issue1, ['Depends'] as Set, issues)
        assert issues.values().collect { it.key } as Set == ['TEST-1', 'TEST-2', 'TEST-3', 'TEST-4'] as Set
        // Links from 4
        issues = [:]
        service.followLinks(config, issue4, ['Depends'] as Set, issues)
        assert issues.values().collect { it.key } as Set == ['TEST-1', 'TEST-2', 'TEST-3', 'TEST-4'] as Set
    }

    static JIRALink createLink(int i, String name, String relation) {
        new JIRALink(
                "TEST-$i",
                "...",
                new JIRAStatus("Open", "..."),
                name,
                relation
        )
    }

    static JIRAIssue createIssue(int i) {
        new JIRAIssue(
                "http://host/browser/TEST-$i",
                "TEST-$i",
                "Issue $i",
                new JIRAStatus("Open", "..."),
                "",
                Time.now(),
                [], [], [], '', []
        )
    }

    private static JIRAConfiguration jiraConfiguration() {
        return new JIRAConfiguration("test", "http://jira", "user", "secret");
    }

}
