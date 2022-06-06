package net.nemerosa.ontrack.extension.jenkins.autoversioning

import net.nemerosa.ontrack.common.BaseException

class JenkinsPostProcessingJobFailureException(
    jenkins: String,
    job: String,
    build: String,
    buildUrl: String,
    result: String?,
) : BaseException("""<a href="$buildUrl">Jenkins post processing at $jenkins / $job / $build failed with status = $result</a>""")
