package net.nemerosa.ontrack.extension.jenkins.client

import java.net.URI

interface JenkinsClient {

    fun getJob(job: String): JenkinsJob

    val info: JenkinsInfo

    /**
     * Runs a job remotely and waits for its completion.
     */
    fun runJob(job: String, parameters: Map<String, String>, retries: Int, retriesDelaySeconds: Int): JenkinsBuild

    /**
     * Fires and forgets a job.
     *
     * @param job Path to the job
     * @param parameters Parameters for the job run
     * @return URI to the queue (null if the job could not be fired)
     */
    fun fireAndForgetJob(job: String, parameters: Map<String, String>): URI?

}