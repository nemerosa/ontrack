package net.nemerosa.ontrack.kdsl.acceptance.tests.jenkins

import net.nemerosa.ontrack.kdsl.acceptance.tests.notifications.AbstractACCDSLNotificationsTestSupport
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.uid
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.waitUntil
import net.nemerosa.ontrack.kdsl.spec.configurations.configurations
import net.nemerosa.ontrack.kdsl.spec.extension.jenkins.JenkinsConfiguration
import net.nemerosa.ontrack.kdsl.spec.extension.jenkins.jenkins
import net.nemerosa.ontrack.kdsl.spec.extension.jenkins.mock.mock
import org.junit.jupiter.api.Test

class ACCJenkinsNotifications : AbstractACCDSLNotificationsTestSupport() {

    @Test
    fun `Jenkins pipeline triggered on promotion`() {
        // Job to call
        val job = uid("job_")
        // Jenkins configuration
        val jenkinsConfName = uid("j_")
        ontrack.configurations.jenkins.create(
            JenkinsConfiguration(
                name = jenkinsConfName,
                url = "any",
                user = "any",
                password = "any",
            )
        )

        project {
            branch {
                val pl = promotion()
                pl.subscribe(
                    channel = "mock-jenkins",
                    channelConfig = mapOf(
                        "config" to jenkinsConfName,
                        "job" to "/mock/${job}",
                        "callMode" to "ASYNC",
                    ),
                    keywords = null,
                    events = listOf(
                        "new_promotion_run"
                    )
                )
                build {
                    promote(pl.name)
                    // Checks that the job was called
                    waitUntil(
                        timeout = 10_000,
                        interval = 500L,
                    ) {
                        ontrack.jenkins.mock.job("/mock/${job}").wasCalled
                    }
                }
            }
        }
    }

}