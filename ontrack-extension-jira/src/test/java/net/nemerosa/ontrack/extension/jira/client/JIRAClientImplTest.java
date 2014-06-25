package net.nemerosa.ontrack.extension.jira.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.nemerosa.ontrack.extension.jira.JIRAConfiguration;
import net.nemerosa.ontrack.extension.jira.model.JIRAIssue;
import net.nemerosa.ontrack.extension.jira.model.JIRAStatus;
import net.nemerosa.ontrack.extension.jira.model.JIRAVersion;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JIRAClientImplTest {

    @Test
    public void parseFromJIRA() {
        LocalDateTime ldt = JIRAClientImpl.parseFromJIRA("2014-06-05T14:39:51.943+0000");
        assertEquals(2014, ldt.getYear());
        assertEquals(6, ldt.getMonthValue());
        assertEquals(5, ldt.getDayOfMonth());
        assertEquals(14, ldt.getHour());
        assertEquals(51, ldt.getSecond());
    }

    @Test
    public void toIssue() throws IOException {
        // Configuration to test with
        JIRAConfiguration config = new JIRAConfiguration("Test", "http://host", "user", "secret");
        // Issue to parse
        JsonNode node = new ObjectMapper().readTree(getClass().getResource("/issue.json"));
        // Parsing the issue
        JIRAIssue issue = JIRAClientImpl.toIssue(config, node);
        // Checking the issue
        assertNotNull(issue);
        assertEquals("PRJ-136", issue.getKey());
        assertEquals("Issue summary", issue.getSummary());
        assertEquals(
                LocalDateTime.of(2014, 6, 18, 15, 12, 52, 369000000),
                issue.getUpdateTime()
        );
        assertEquals(
                new JIRAStatus("Closed", "http://jira/images/icons/statuses/closed.png"),
                issue.getStatus()
        );
        assertEquals(
                Arrays.asList(
                        new JIRAVersion(
                                "1.2",
                                false
                        )
                ),
                issue.getFixVersions()
        );
        assertEquals(
                "dcoraboeuf",
                issue.getAssignee()
        );
        assertEquals(
                Arrays.asList(
                        new JIRAVersion(
                                "1.0",
                                true
                        )
                ),
                issue.getAffectedVersions()
        );
    }

}
