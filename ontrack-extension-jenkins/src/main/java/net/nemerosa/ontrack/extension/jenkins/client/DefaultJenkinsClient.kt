package net.nemerosa.ontrack.extension.jenkins.client

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import net.nemerosa.ontrack.common.untilTimeout
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate
import java.net.URI
import java.time.Duration

class DefaultJenkinsClient(
    url: String,
    private val client: RestTemplate,
) : AbstractJenkinsClient(
    url
) {

    private val logger: Logger = LoggerFactory.getLogger(DefaultJenkinsClient::class.java)

    private val crumbs: JenkinsCrumbs by lazy {
        runBlocking {
            untilTimeout("Getting Jenkins authentication crumbs") {
                client.getForObject("/crumbIssuer/api/json", JenkinsCrumbs::class.java)
            }
        }
    }

    override val info: JenkinsInfo
        get() =
            client.getForObject("/api/json", JenkinsInfo::class.java) ?: error("Cannot get Jenkins info")

    override fun fireAndForgetJob(job: String, parameters: Map<String, String>): URI? {
        val path = getJobPath(job)
        return launchJob(path, parameters)
    }

    private fun launchJob(path: String, parameters: Map<String, String>): URI? {
        return runBlocking {
            logger.debug("run,path={},parameters={}", path, parameters)

            // Build without parameters
            if (parameters.isEmpty()) {
                withContext(Dispatchers.IO) {
                    client.postForLocation(
                        "$path/build",
                        ""
                    )
                }
            }

            // Build with parameters
            else {
                // Query
                val map = LinkedMultiValueMap<String, Any>()
                parameters.forEach { (key, value) ->
                    map.add(key, value)
                }

                // Headers (form)
                val headers = createCSRFHeaders()
                headers.contentType = MediaType.MULTIPART_FORM_DATA

                // Request to send
                val requestEntity = HttpEntity(map, headers)

                // Launches the job with parameters and get the queue item
                withContext(Dispatchers.IO) {
                    client.postForLocation(
                        "$path/buildWithParameters",
                        requestEntity
                    )
                }
            }
        }
    }

    override fun runJob(
        job: String,
        parameters: Map<String, String>,
        retries: Int,
        retriesDelaySeconds: Int,
        buildFeedback: (build: JenkinsBuild) -> Unit,
    ): JenkinsBuild {
        val retriesDelay = Duration.ofSeconds(retriesDelaySeconds.toLong())
        return runBlocking {

            val path = getJobPath(job)

            val queueItemURI: URI? = launchJob(path, parameters)

            if (queueItemURI == null) {
                error("Cannot fire job $path")
            } else {
                // We must now monitor the state of the queue item
                // Until `cancelled` is true, or `executable` contains a valid link
                val executable: JenkinsBuildId =
                    untilTimeout("Waiting for $queueItemURI to be scheduled", retries, retriesDelay) {
                        logger.debug("queued={},job={},path={},parameters={}", queueItemURI, job, path, parameters)
                        getQueueStatus(queueItemURI)
                    }
                // Waits for its completion
                untilTimeout("Waiting for build completion at ${executable.url(path)}", retries, retriesDelay) {
                    logger.debug("build={},job={},path={},parameters={}", executable.number, job, path, parameters)
                    val build = getBuildStatus(path, executable)
                    // Feedback
                    if (build != null) {
                        buildFeedback(build)
                    }
                    // Going on
                    build
                }
            }
        }
    }

    private fun createCSRFHeaders(): HttpHeaders {
        val headers = HttpHeaders()
        headers.add(crumbs.crumbRequestField, crumbs.crumb)
        return headers
    }

    private fun getQueueStatus(queueItemURI: URI): JenkinsBuildId? {
        val queueItem: JenkinsQueueItem? = client.getForObject("$queueItemURI/api/json", JenkinsQueueItem::class.java)
        return if (queueItem != null) {
            when {
                queueItem.cancelled != null && queueItem.cancelled -> throw JenkinsJobCancelledException(queueItemURI.toString())
                queueItem.executable != null -> queueItem.executable
                else -> null
            }
        } else {
            null
        }
    }

    private fun getBuildStatus(job: String, buildId: JenkinsBuildId): JenkinsBuild? {
        val buildInfoUrl = "${buildId.url(job)}/api/json"
        val build: JenkinsBuild? = client.getForObject(buildInfoUrl, JenkinsBuild::class.java)
        return build?.takeIf { !build.building }
    }
}