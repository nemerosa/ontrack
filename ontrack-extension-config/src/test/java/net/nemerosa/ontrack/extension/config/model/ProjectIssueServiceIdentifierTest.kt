package net.nemerosa.ontrack.extension.config.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ProjectIssueServiceIdentifierTest {

    @Test
    fun representation() {
        val id = ProjectIssueServiceIdentifier("jira", "JIRA")
        assertEquals("jira//JIRA", id.toRepresentation())
    }

    @Test
    fun parse() {
        val id = ProjectIssueServiceIdentifier.parse("jira//JIRA")
        assertEquals("jira", id?.serviceId)
        assertEquals("JIRA", id?.serviceName)
    }

    @Test
    fun parse_null_when_wrong_format() {
        assertNull(ProjectIssueServiceIdentifier.parse("jira:JIRA"))
    }

    @Test
    fun parse_null_when_empty() {
        assertNull(ProjectIssueServiceIdentifier.parse(""))
    }

}