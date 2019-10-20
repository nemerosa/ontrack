package net.nemerosa.ontrack.extension.svn.model

data class SVNLocation(
        val path: String,
        val revision: Long
) {
    fun withRevision(revision: Long): SVNLocation {
        return SVNLocation(path, revision)
    }
}
