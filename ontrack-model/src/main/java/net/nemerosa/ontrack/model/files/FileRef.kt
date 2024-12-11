package net.nemerosa.ontrack.model.files

data class FileRef(
    val protocol: String,
    val path: String,
) {
    companion object {

        private val regex = "^([a-z][a-z0-9]+):(.*)\$".toRegex()

        fun parseUri(uri: String): FileRef? {
            val m = regex.matchEntire(uri) ?: return null
            return FileRef(
                protocol = m.groupValues[1],
                path = m.groupValues[2],
            )
        }
    }
}
