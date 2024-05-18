package net.nemerosa.ontrack.extension.jenkins

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.DocumentationIgnore
import net.nemerosa.ontrack.model.docs.DocumentationUseSuper

/**
 * Property to associate a Jenkins job link to a project entity like a branch.
 *
 * @property configuration Associated Jenkins configuration
 * @property job Path to the Jenkins job, relative to root. It may or may not include `/job` URL separators.
 */
@DocumentationUseSuper
open class JenkinsJobProperty(
    configuration: JenkinsConfiguration,
    @APIDescription("Path to the Jenkins job, relative to root. It may or may not include `/job` URL separators.")
    val job: String
) : AbstractJenkinsProperty(configuration) {
    /**
     * Derived property: the full URL to the Jenkins job.
     */
    @DocumentationIgnore
    open val url: String =
        "${configuration.url}/job/${withFolders(job)}"

    /**
     * Derived property: job path as separate components
     */
    @DocumentationIgnore
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