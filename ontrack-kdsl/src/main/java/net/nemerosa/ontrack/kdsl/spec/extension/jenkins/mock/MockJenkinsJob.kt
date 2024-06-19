package net.nemerosa.ontrack.kdsl.spec.extension.jenkins.mock

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.parseOrNull

class MockJenkinsJob(
    connector: Connector,
    val config: String,
    val path: String,
) : Connected(connector) {

    val jenkinsJob: JenkinsJob?
        get() =
            connector.get("/extension/jenkins/mock/${config}/job?path=$path").body.parseOrNull<JenkinsJob>()

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class JenkinsJob(
        val builds: List<JenkinsBuild>,
    ) {
        val wasCalled: Boolean = builds.isNotEmpty()
        val lastBuild: JenkinsBuild = builds.first()
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class JenkinsBuild(
        val number: Int,
        val parameters: Map<String, String>,
    )

}