package net.nemerosa.ontrack.extension.git.model

interface IndexableBuildGitCommitLink<T> : BuildGitCommitLink<T> {
    fun getBuildNameFromTagName(tagName: String, data: T): String?
}
