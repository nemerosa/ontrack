package net.nemerosa.ontrack.extension.svn.model

class SVNLocation(
        val path: String,
        val revision: Long
) {
    fun withRevision(revision: Long) = SVNLocation(path, revision)
}

