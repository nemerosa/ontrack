package net.nemerosa.ontrack.extension.jenkins.client

data class JenkinsBuildId(
        val number: Int
) {
    fun url(path: String) = "$path/$number"
}
