package net.nemerosa.ontrack.extension.git.model

data class GitBuildInfo(
    val placeholder: String = ""
) {
    companion object {
        val INSTANCE: GitBuildInfo = GitBuildInfo()
    }
}
