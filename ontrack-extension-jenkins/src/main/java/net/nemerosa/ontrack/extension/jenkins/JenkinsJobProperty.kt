package net.nemerosa.ontrack.extension.jenkins

/**
 * Property to associate a Jenkins job link to a project entity like a branch.
 *
 * @property configuration Associated Jenkins configuration
 * @property job Path to the Jenkins job, relative to root. It may or may not include `/job` URL separators.
 */
open class JenkinsJobProperty(
    configuration: JenkinsConfiguration,
    val job: String
) : AbstractJenkinsProperty(configuration) {
    /**
     * Derived property: the full URL to the Jenkins job.
     */
    open val url: String =
        "${configuration.url}/job/${withFolders(job)}"

    /**
     * Derived property: job path as separate components
     */
    val pathComponents: List<String> = withFolders(job).split("/job/".toRegex())

    /**
     * Two pass replacement /job/ --> / --> /job/ in order to preserve the use of /job/
     */
    private fun withFolders(path: String): String =
        path
            .removeSuffix("/")
            .replace("/job/", "/")
            .replace("/", "/job/")

}