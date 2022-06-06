package net.nemerosa.ontrack.extension.jenkins.client

import net.nemerosa.ontrack.common.BaseException

class JenkinsJobCancelledException(path: String) : BaseException("Job at $path has been cancelled.")
