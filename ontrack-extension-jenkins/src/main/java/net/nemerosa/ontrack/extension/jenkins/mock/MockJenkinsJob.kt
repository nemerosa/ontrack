package net.nemerosa.ontrack.extension.jenkins.mock

data class MockJenkinsJob(
    val job: String,
) {

    val builds = mutableListOf<MockJenkinsBuild>()

    fun build(parameters: Map<String, String>): MockJenkinsBuild {
        val build = MockJenkinsBuild(
            number = builds.size + 1,
            parameters = parameters,
        )
        builds += build
        return build
    }
}