package net.nemerosa.ontrack.extension.jenkins.client

data class JenkinsBuild(
    val id: String,
    val building: Boolean,
    val url: String,
    val result: String?,
) {
    val successful: Boolean = !building && result == JENKINS_BUILD_RESULT_SUCCESS

    companion object {
        private const val JENKINS_BUILD_RESULT_SUCCESS = "SUCCESS"
    }
}