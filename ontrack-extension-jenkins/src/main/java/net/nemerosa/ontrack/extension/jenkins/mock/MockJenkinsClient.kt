package net.nemerosa.ontrack.extension.jenkins.mock

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
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
        retriesDelaySeconds: Int,
        buildFeedback: (build: JenkinsBuild) -> Unit,
    ): JenkinsBuild {
        val build = jobs.getOrPut(job) { MockJenkinsJob(job) }.build(parameters)
        val result = parameters[PARAM_RESULT] ?: "SUCCESS"
        val waiting: Long? = parameters[PARAM_WAITING]?.toLong()
        if (waiting != null) {
            // Creating the ongoing build
            val ongoingBuild = JenkinsBuild(
                id = build.number.toString(),
                building = true,
                url = "$url/$job/${build.number}",
                result = null,
            )
            // Feedback
            buildFeedback(ongoingBuild)
            // Waiting
            runBlocking {
                delay(waiting)
            }
        }
        return JenkinsBuild(
            id = build.number.toString(),
            building = false,
            url = "$url/$job/${build.number}",
            result = result,
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

    companion object {
        private const val PARAM_RESULT = "result"
        private const val PARAM_WAITING = "waiting"
    }
}