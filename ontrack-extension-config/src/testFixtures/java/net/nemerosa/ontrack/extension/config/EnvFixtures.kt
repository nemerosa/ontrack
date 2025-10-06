package net.nemerosa.ontrack.extension.config

object EnvFixtures {

    fun generic(
        scmBranch: String = "release/1.51",
        extraEnv: Map<String, String> = emptyMap(),
    ) = mapOf(
        "PROJECT_NAME" to "yontrack",
        "BRANCH_NAME" to scmBranch,
        "BUILD_NUMBER" to "23",
        "BUILD_REVISION" to "abcd123",
        "VERSION" to "5.1.12",
    ) + extraEnv

    fun jenkins(
        scmBranch: String = "release/1.51",
        extraEnv: Map<String, String> = emptyMap(),
    ) = mapOf(
        "JENKINS_URL" to "https://jenkins.dev.yontrack.com",
        "GIT_URL" to "https://github.com/nemerosa/ontrack.git",
        "BRANCH_NAME" to scmBranch,
    ) + extraEnv

}