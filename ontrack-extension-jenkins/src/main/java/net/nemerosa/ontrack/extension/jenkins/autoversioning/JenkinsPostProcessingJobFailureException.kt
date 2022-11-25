package net.nemerosa.ontrack.extension.jenkins.autoversioning

import net.nemerosa.ontrack.common.BaseException
import net.nemerosa.ontrack.extension.av.postprocessing.PostProcessingFailureException

class JenkinsPostProcessingJobFailureException(
    jenkins: String,
    job: String,
    build: String,
    buildUrl: String,
    result: String?,
) : BaseException(
    """Jenkins post processing at $jenkins / $job / $build failed with status = $result"""
), PostProcessingFailureException {

    override val link: String = buildUrl
}
