package net.nemerosa.ontrack.extension.jira.service

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.jira.JIRAConfiguration
import net.nemerosa.ontrack.extension.jira.client.JIRAClient
import net.nemerosa.ontrack.extension.jira.model.JIRAIssue
import net.nemerosa.ontrack.extension.jira.model.JIRALink
import net.nemerosa.ontrack.extension.jira.model.JIRAStatus
import org.junit.Test

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class JIRAServiceImplTest {

    @Test
    void 'Following links'() {
        // Configuration to test with
        JIRAConfiguration config = new JIRAConfiguration("Test", "http://host", "user", "secret");
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
        JIRAClient jiraClient = mock(JIRAClient)
        when(jiraClient.getIssue('TEST-1', config)).thenReturn(issue1)
        when(jiraClient.getIssue('TEST-2', config)).thenReturn(issue2)
        when(jiraClient.getIssue('TEST-3', config)).thenReturn(issue3)
        when(jiraClient.getIssue('TEST-4', config)).thenReturn(issue4)

        // Service
        // JIRAService jiraService = new JIRAServiceImpl(jiraClient)

        // Links from 1
        def issues = [:]
        jiraService.followLinks(config, issue1, ['Depends'] as Set, issues)
        assert issues.values().collect { it.key } as Set == ['TEST-1', 'TEST-2', 'TEST-3', 'TEST-4'] as Set
        // Links from 4
        issues = [:]
        jiraService.followLinks(config, issue4, ['Depends'] as Set, issues)
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
}
