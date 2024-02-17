package net.nemerosa.ontrack.extension.scm.changelog

/**
 * Defines the scope of a link (project and optional qualifier) when getting a deep change log.
 *
 * @param project Project name
 * @param qualifier Link qualifier
 */
data class ProjectLink(
    val project: String,
    val qualifier: String,
) {

    companion object {
        /**
         * The text to parse is either the project name or the project name
         * and a qualifier separated by a colon (:).
         */
        fun parse(text: String): ProjectLink {
            val tokens = text.split(":").map { it.trim() }
            return when (tokens.size) {
                1 -> ProjectLink(tokens.first(), "")
                2 -> ProjectLink(tokens[0], tokens[1])
                else -> throw ProjectLinkParsingException(text)
            }
        }
    }

}
