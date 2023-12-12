package net.nemerosa.ontrack.kdsl.spec.extension.jenkins.mock

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.parseOrNull

class MockJenkinsJob(
    connector: Connector,
    val path: String,
) : Connected(connector) {

    val wasCalled: Boolean
        get() {
            val job = connector.get("/extension/jenkins/mock/job?path=$path").body.parseOrNull<JenkinsJob>()
            return job != null && job.builds.isNotEmpty()
        }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class JenkinsJob(
        val builds: List<JenkinsBuild>,
    )

    @JsonIgnoreProperties(ignoreUnknown = true)
    private data class JenkinsBuild(
        val number: Int,
    )

}