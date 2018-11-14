package net.nemerosa.ontrack.git.model

import org.eclipse.jgit.revwalk.RevCommit

class GitDiffEntry(
        val changeType: GitChangeType,
        val oldPath: String,
        val newPath: String
) {

    val referencePath: String
        get() = when (changeType) {
            GitChangeType.DELETE -> oldPath
            else -> newPath
        }

    fun getReferenceId(from: RevCommit, to: RevCommit): String = when (changeType) {
        GitChangeType.DELETE -> from.id.name()
        else -> to.id.name()
    }
}
