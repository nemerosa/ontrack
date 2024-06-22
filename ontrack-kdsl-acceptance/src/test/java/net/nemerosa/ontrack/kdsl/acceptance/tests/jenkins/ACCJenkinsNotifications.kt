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
                    name = "Test",
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
                        val jenkinsJob = ontrack.jenkins.mock.job(jenkinsConfName, "/mock/${job}").jenkinsJob
                        jenkinsJob != null && jenkinsJob.wasCalled
                    }
                }
            }
        }
    }

    @Test
    fun `Jenkins pipeline triggered on promotion with parameters`() {
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
                    name = "Test",
                    channel = "mock-jenkins",
                    channelConfig = mapOf(
                        "config" to jenkinsConfName,
                        "job" to "/mock/${job}",
                        "callMode" to "ASYNC",
                        "parameters" to listOf(
                            mapOf(
                                "name" to "PROMOTION",
                                "value" to "\${promotionLevel}",
                            )
                        )
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
                        val jenkinsJob = ontrack.jenkins.mock.job(jenkinsConfName, "/mock/${job}").jenkinsJob
                        jenkinsJob != null && jenkinsJob.wasCalled && jenkinsJob.lastBuild.parameters == mapOf(
                            "PROMOTION" to pl.name,
                        )
                    }
                }
            }
        }
    }

}