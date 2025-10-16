package net.nemerosa.ontrack.extension.config

object EnvFixtures {

    fun generic(
        scmBranch: String = TEST_BRANCH,
        extraEnv: Map<String, String> = emptyMap(),
    ) = mapOf(
        "PROJECT_NAME" to "yontrack",
        "BRANCH_NAME" to scmBranch,
        "BUILD_NUMBER" to "23",
        "BUILD_REVISION" to "abcd123",
        "VERSION" to "5.1.12",
    ) + extraEnv

    fun jenkins(
        scmBranch: String = TEST_BRANCH,
        extraEnv: Map<String, String> = emptyMap(),
    ) = mapOf(
        "JENKINS_URL" to "https://jenkins.dev.yontrack.com",
        "GIT_URL" to "https://github.com/nemerosa/ontrack.git",
        "BRANCH_NAME" to scmBranch,
    ) + extraEnv

    fun gitHub(
        projectName: String = "yontrack",
        scmBranch: String = TEST_BRANCH,
        runNumber: Long = 96L,
        extraEnv: Map<String, String> = emptyMap(),
    ) = mapOf(
        "GITHUB_SERVER_URL" to "https://github.com",
        "GITHUB_REPOSITORY" to "yontrack/$projectName",
        "GITHUB_REF_NAME" to scmBranch,
        "GITHUB_ACTIONS" to "true",
        "GITHUB_RUN_NUMBER" to runNumber.toString(),
        "GITHUB_SHA" to TEST_COMMIT,
    ) + extraEnv

    const val TEST_BRANCH = "release/1.51"
    const val TEST_COMMIT = "7c0b1745f513b9162791651582c0044d7b6d2a83"

}