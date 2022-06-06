package net.nemerosa.ontrack.extension.jenkins.client

data class JenkinsQueueItem(
        val cancelled: Boolean?,
        val executable: JenkinsBuildId?,
)

