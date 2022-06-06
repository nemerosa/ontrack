package net.nemerosa.ontrack.extension.jenkins.client

interface JenkinsClient {

    fun getJob(job: String): JenkinsJob

    val info: JenkinsInfo

    /**
     * Runs a job remotely.
     */
    fun runJob(job: String, parameters: Map<String, String>, retries: Int, retriesDelaySeconds: Int): JenkinsBuild

}