package net.nemerosa.ontrack.extension.config.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ProjectIssueServiceIdentifierTest {

    @Test
    fun representation() {
        val id = ProjectIssueServiceIdentifier("jira", "JIRA")
        assertEquals("jira//JIRA", id.toRepresentation())
    }

}