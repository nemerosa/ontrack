package net.nemerosa.ontrack.kdsl.acceptance.tests.jenkins

import net.nemerosa.ontrack.kdsl.acceptance.tests.AbstractACCDSLTestSupport
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.spec.configurations.configurations
import net.nemerosa.ontrack.kdsl.spec.extension.jenkins.JenkinsConfiguration
import net.nemerosa.ontrack.kdsl.spec.extension.jenkins.jenkins
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ACCJenkinsConfigurations : AbstractACCDSLTestSupport() {

    @Test
    fun `Configuration - Jenkins`() {
        val name = uid("j-")
        ontrack.configurations.jenkins.create(
            JenkinsConfiguration(
                name = name,
                url = "http://jenkins",
            )
        )
        assertNotNull(
            ontrack.configurations.jenkins.findByName(name),
            "Jenkins configuration created"
        )
    }

    @Test
    fun `Configuration of Jenkins with password`() {
        val name = uid("j-")
        ontrack.configurations.jenkins.create(
            JenkinsConfiguration(
                name = name,
                url = "http://jenkins",
                user = "user",
                password = "secret"
            )
        )
        assertNotNull(
            ontrack.configurations.jenkins.findByName(name),
            "Jenkins configuration created"
        ) {
            assertEquals("", it.password)
        }
    }

}