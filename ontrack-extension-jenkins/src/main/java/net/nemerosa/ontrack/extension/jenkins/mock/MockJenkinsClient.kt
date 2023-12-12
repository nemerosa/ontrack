package net.nemerosa.ontrack.extension.jenkins.mock

import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.extension.jenkins.client.JenkinsBuild
import net.nemerosa.ontrack.extension.jenkins.client.JenkinsClient
import net.nemerosa.ontrack.extension.jenkins.client.JenkinsInfo
import net.nemerosa.ontrack.extension.jenkins.client.JenkinsJob
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.net.URI

@Component
@Profile(RunProfile.ACC)
class MockJenkinsClient : JenkinsClient {

    val jobs = mutableMapOf<String, MockJenkinsJob>()

    override fun getJob(job: String): JenkinsJob {
        TODO("Not yet implemented")
    }

    override val info: JenkinsInfo
        get() = TODO("Not yet implemented")

    override fun runJob(
        job: String,
        parameters: Map<String, String>,
        retries: Int,
        retriesDelaySeconds: Int
    ): JenkinsBuild {
        TODO("Not yet implemented")
    }

    override fun fireAndForgetJob(job: String, parameters: Map<String, String>): URI? {
        if (parameters["queued"] == "false") {
            return null
        } else {
            jobs.getOrPut(job) { MockJenkinsJob(job) }.build(parameters)
            return URI("/queue/$job")
        }
    }
}