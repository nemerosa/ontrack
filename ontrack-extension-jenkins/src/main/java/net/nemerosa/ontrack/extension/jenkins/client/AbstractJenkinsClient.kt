package net.nemerosa.ontrack.extension.jenkins.client

abstract class AbstractJenkinsClient(
    protected val url: String,
): JenkinsClient {

    final override fun getJob(job: String): JenkinsJob {
        val jobPath = getJobPath(job)
        val jobUrl = "$url$jobPath"
        val jobName = jobPath.substringAfterLast("/")
        return JenkinsJob(
            jobName,
            jobUrl
        )
    }

    protected fun getJobPath(job: String): String {
        val path = job.replace("/job/".toRegex(), "/").replace("/".toRegex(), "/job/")
        return "/job/$path"
    }

}