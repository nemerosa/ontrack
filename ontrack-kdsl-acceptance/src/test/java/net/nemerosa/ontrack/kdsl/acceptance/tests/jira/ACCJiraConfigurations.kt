package net.nemerosa.ontrack.kdsl.acceptance.tests.jira

import net.nemerosa.ontrack.kdsl.acceptance.tests.AbstractACCDSLTestSupport
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.spec.configurations.configurations
import net.nemerosa.ontrack.kdsl.spec.extension.jira.JiraConfiguration
import net.nemerosa.ontrack.kdsl.spec.extension.jira.jira
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Testing Jira notifications in a workflow.
 */
class ACCJiraConfigurations : AbstractACCDSLTestSupport() {

    @Test
    fun `Creation of a configuration with username and password`() {
        val name = uid("jira-")
        ontrack.configurations.jira.create(
            JiraConfiguration(
                name = name,
                url = "https://jira",
                user = "some-user",
                password = "some-password"
            )
        )
        assertNotNull(ontrack.configurations.jira.findByName(name)) {
            assertEquals("some-user", it.user)
            assertEquals("", it.password)
        }
    }

    @Test
    fun `Creation of a configuration with token`() {
        val name = uid("jira-")
        ontrack.configurations.jira.create(
            JiraConfiguration(
                name = name,
                url = "https://jira",
                password = "some-token"
            )
        )
        assertNotNull(ontrack.configurations.jira.findByName(name)) {
            assertEquals("", it.user)
            assertEquals("", it.password)
        }
    }

}