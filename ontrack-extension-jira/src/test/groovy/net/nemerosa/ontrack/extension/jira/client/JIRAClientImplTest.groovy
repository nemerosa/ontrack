package net.nemerosa.ontrack.extension.jira.client

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import net.nemerosa.ontrack.extension.jira.JIRAConfiguration
import net.nemerosa.ontrack.extension.jira.model.JIRAIssue
import net.nemerosa.ontrack.extension.jira.model.JIRALink
import net.nemerosa.ontrack.extension.jira.model.JIRAStatus
import net.nemerosa.ontrack.extension.jira.model.JIRAVersion
import org.junit.Test

import java.time.LocalDateTime

import static org.junit.Assert.assertEquals

class JIRAClientImplTest {

    @Test
    void parseFromJIRA() {
        LocalDateTime ldt = JIRAClientImpl.parseFromJIRA("2014-06-05T14:39:51.943+0000");
        assertEquals(2014, ldt.getYear());
        assertEquals(6, ldt.getMonthValue());
        assertEquals(5, ldt.getDayOfMonth());
        assertEquals(14, ldt.getHour());
        assertEquals(51, ldt.getSecond());
    }

    @Test
    void toIssue() {
        // Configuration to test with
        JIRAConfiguration config = new JIRAConfiguration("Test", "http://jira", "user", "secret");
        // Issue to parse
        JsonNode node = new ObjectMapper().readTree(getClass().getResource("/issue.json"));
        // Parsing the issue
        JIRAIssue issue = JIRAClientImpl.toIssue(config, node);
        // Checking the issue
        assert issue != null
        assert issue.key == 'PRJ-136'
        assert issue.summary == 'Issue summary'
        assert issue.updateTime == LocalDateTime.of(2014, 6, 18, 15, 12, 52, 369000000)
        assert issue.status == new JIRAStatus('Closed', 'http://jira/images/icons/statuses/closed.png')
        assert issue.fixVersions == [new JIRAVersion('1.2', false)]
        assert issue.assignee == 'dcoraboeuf'
        assert issue.affectedVersions == [new JIRAVersion('1.0', true)]
    }

    @Test
    void 'toIssue with one inward link'() {
        // Configuration to test with
        JIRAConfiguration config = new JIRAConfiguration("Test", "http://jira", "user", "secret");
        // Issue to parse
        JsonNode node = new ObjectMapper().readTree(getClass().getResource("/issue-link-inward.json"));
        // Parsing the issue
        JIRAIssue issue = JIRAClientImpl.toIssue(config, node);
        // Checking the issue
        assert issue != null
        assert issue.key == 'PRJ-136'
        assert issue.summary == 'Issue summary'
        assert issue.updateTime == LocalDateTime.of(2014, 6, 18, 15, 12, 52, 369000000)
        assert issue.status == new JIRAStatus('Closed', 'http://jira/images/icons/statuses/closed.png')
        assert issue.fixVersions == [new JIRAVersion('1.2', false)]
        assert issue.assignee == 'dcoraboeuf'
        assert issue.affectedVersions == [new JIRAVersion('1.0', true)]
        assert issue.links == [
                new JIRALink(
                        'PRJ-900',
                        'http://jira/browse/PRJ-900',
                        new JIRAStatus('Open', 'http://jira/images/icons/statuses/open.png'),
                        'Blocks',
                        'is blocked by'
                )
        ]
    }

    @Test
    void 'toIssue with one outward link'() {
        // Configuration to test with
        JIRAConfiguration config = new JIRAConfiguration("Test", "http://jira", "user", "secret");
        // Issue to parse
        JsonNode node = new ObjectMapper().readTree(getClass().getResource("/issue-link-outward.json"));
        // Parsing the issue
        JIRAIssue issue = JIRAClientImpl.toIssue(config, node);
        // Checking the issue
        assert issue != null
        assert issue.key == 'PRJ-136'
        assert issue.summary == 'Issue summary'
        assert issue.updateTime == LocalDateTime.of(2014, 6, 18, 15, 12, 52, 369000000)
        assert issue.status == new JIRAStatus('Closed', 'http://jira/images/icons/statuses/closed.png')
        assert issue.fixVersions == [new JIRAVersion('1.2', false)]
        assert issue.assignee == 'dcoraboeuf'
        assert issue.affectedVersions == [new JIRAVersion('1.0', true)]
        assert issue.links == [
                new JIRALink(
                        'PRJ-900',
                        'http://jira/browse/PRJ-900',
                        new JIRAStatus('Open', 'http://jira/images/icons/statuses/open.png'),
                        'Blocks',
                        'blocks'
                )
        ]
    }

    @Test
    void 'toIssue with two links'() {
        // Configuration to test with
        JIRAConfiguration config = new JIRAConfiguration("Test", "http://jira", "user", "secret");
        // Issue to parse
        JsonNode node = new ObjectMapper().readTree(getClass().getResource("/issue-link-both.json"));
        // Parsing the issue
        JIRAIssue issue = JIRAClientImpl.toIssue(config, node);
        // Checking the issue
        assert issue != null
        assert issue.key == 'PRJ-136'
        assert issue.summary == 'Issue summary'
        assert issue.updateTime == LocalDateTime.of(2014, 6, 18, 15, 12, 52, 369000000)
        assert issue.status == new JIRAStatus('Closed', 'http://jira/images/icons/statuses/closed.png')
        assert issue.fixVersions == [new JIRAVersion('1.2', false)]
        assert issue.assignee == 'dcoraboeuf'
        assert issue.affectedVersions == [new JIRAVersion('1.0', true)]
        assert issue.links == [
                new JIRALink(
                        'PRJ-900',
                        'http://jira/browse/PRJ-900',
                        new JIRAStatus('Open', 'http://jira/images/icons/statuses/open.png'),
                        'Blocks',
                        'blocks'
                ),
                new JIRALink(
                        'PRJ-901',
                        'http://jira/browse/PRJ-901',
                        new JIRAStatus('Open', 'http://jira/images/icons/statuses/open.png'),
                        'Blocks',
                        'is blocked by'
                )
        ]
    }

}
