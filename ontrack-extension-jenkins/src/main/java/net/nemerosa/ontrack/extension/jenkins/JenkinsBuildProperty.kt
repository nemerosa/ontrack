package net.nemerosa.ontrack.extension.jenkins

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.DocumentationIgnore
import net.nemerosa.ontrack.model.docs.DocumentationUseSuper

/**
 * This property associates a build number with a Jenkins job.
 */
@DocumentationUseSuper
open class JenkinsBuildProperty(
    configuration: JenkinsConfiguration,
    job: String,
    @APIDescription("Number of the build")
    val build: Int,
) : JenkinsJobProperty(configuration, job) {

    /**
     * Derived property: the full URL to the Jenkins build.
     */
    @DocumentationIgnore
    override val url: String
        get() = super.url + "/" + build

}
