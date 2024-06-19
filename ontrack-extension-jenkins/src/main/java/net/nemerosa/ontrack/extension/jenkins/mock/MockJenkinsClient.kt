package net.nemerosa.ontrack.extension.jenkins.mock

import net.nemerosa.ontrack.extension.jenkins.JenkinsConfiguration
import net.nemerosa.ontrack.extension.jenkins.client.AbstractJenkinsClient
import net.nemerosa.ontrack.extension.jenkins.client.JenkinsBuild
import net.nemerosa.ontrack.extension.jenkins.client.JenkinsInfo
import java.net.URI

class MockJenkinsClient(
    configuration: JenkinsConfiguration,
) : AbstractJenkinsClient(
    url = configuration.url,
) {

    val jobs = mutableMapOf<String, MockJenkinsJob>()

    override val info: JenkinsInfo
        get() = TODO("Not yet implemented")

    override fun runJob(
        job: String,
        parameters: Map<String, String>,
        retries: Int,
        retriesDelaySeconds: Int
    ): JenkinsBuild {
        val build = jobs.getOrPut(job) { MockJenkinsJob(job) }.build(parameters)
        return JenkinsBuild(
            id = build.number.toString(),
            building = false,
            url = "$url/$job/${build.number}",
            result = "SUCCESS",
        )
    }

    override fun fireAndForgetJob(job: String, parameters: Map<String, String>): URI? {
        if (parameters["queued"] == "false") {
            return null
        } else {
            jobs.getOrPut(job) { MockJenkinsJob(job) }.build(parameters)
            return URI("$url/queue/$job")
        }
    }
}