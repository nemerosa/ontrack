package net.nemerosa.ontrack.extension.scm.service

data class SCMRef(
    val protocol: String,
    val type: String,
    val config: String,
    val ref: String,
) {
    companion object {

        const val PROTOCOL = "scm"
        private val regex = "^${PROTOCOL}:\\/\\/([^\\/]*)\\/([^\\/]*)\\/(.*)\$".toRegex()

        /**
         * The URI must be formatted as `scm://<type>/<config>/<ref>` where:
         *
         * * type is the SCM type: github, bitbucket-server, etc.
         * * config is the name of the configuration for the SCM type as stored in Ontrack
         * * ref is the reference to the document inside the SCM. This may be additionally formatted according the SCM.
         */
        fun parseUri(uri: String): SCMRef? {
            val m = regex.matchEntire(uri) ?: return null
            return SCMRef(
                protocol = PROTOCOL,
                type = m.groupValues[1],
                config = m.groupValues[2],
                ref = m.groupValues[3],
            )
        }
    }
}
