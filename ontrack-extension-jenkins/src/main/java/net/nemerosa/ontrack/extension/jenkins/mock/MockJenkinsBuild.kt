package net.nemerosa.ontrack.extension.jenkins.mock

data class MockJenkinsBuild(
    val number: Int,
    val parameters: Map<String, String>,
)