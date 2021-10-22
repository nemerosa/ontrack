package net.nemerosa.ontrack.extension.jenkins

/**
 * This property associates a build number with a Jenkins job.
 */
open class JenkinsBuildProperty(
    configuration: JenkinsConfiguration,
    job: String,
    val build: Int,
) : JenkinsJobProperty(configuration, job) {

    /**
     * Derived property: the full URL to the Jenkins build.
     */
    override val url: String
        get() = super.url + "/" + build

}
