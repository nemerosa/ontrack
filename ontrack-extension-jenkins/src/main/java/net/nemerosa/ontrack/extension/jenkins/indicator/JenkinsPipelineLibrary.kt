package net.nemerosa.ontrack.extension.jenkins.indicator

/**
 * Reference to a Jenkins pipeline library
 *
 * @property name Name or reference for this library
 * @property version Version of the library if available
 */
data class JenkinsPipelineLibrary(
    val name: String,
    val version: JenkinsPipelineLibraryVersion?
) {

    constructor(name: String, version: String): this(name, JenkinsPipelineLibraryVersion(version))

    companion object {

        private const val LIBRARY_EXPRESSION = "@Library\\((.+)\\)"

        @Suppress("RegExpRedundantEscape")
        private const val LIBRARY_ID = "([a-zA-Z0-9_-]+)(?:@([a-zA-Z0-9\\.\\/_-]+))?"

        private val libraryExpression: Regex = LIBRARY_EXPRESSION.toRegex()

        private val libraryId: Regex = LIBRARY_ID.toRegex()

        /**
         * Parses and extracts pipeline libraries from the content of a Jenkinsfile.
         */
        fun extractLibraries(jenkinsfile: String): List<JenkinsPipelineLibrary> {
            val lines = jenkinsfile.lines()
            return lines.flatMap { line ->
                extractLibrariesFromLine(line)
            }
        }

        private fun extractLibrariesFromLine(line: String): List<JenkinsPipelineLibrary> {
            // Gets a library expression
            val match = libraryExpression.find(line)
            return if (match != null) {
                val libraries = match.groupValues[1].trim()
                if (libraries.startsWith("[")) {
                    // Multiple libraries
                    val ids = libraries.trimStart('[').trimEnd(']').split(",").map { it.trim() }
                    // Parsing
                    ids.mapNotNull { parseLibrary(it) }
                } else {
                    // Single library
                    val library = parseLibrary(libraries)
                    listOfNotNull(library)
                }
            } else {
                emptyList()
            }
        }

        private fun parseLibrary(expression: String): JenkinsPipelineLibrary? {
            val unquoted = expression.trim('\'', '"')
            val matcher = libraryId.matchEntire(unquoted)
            return if (matcher != null) {
                val name = matcher.groupValues[1]
                val version = if (matcher.groupValues.size > 1) {
                    matcher.groupValues[2]
                } else {
                    null
                }
                JenkinsPipelineLibrary(name, version?.takeIf { it.isNotBlank() }?.run { JenkinsPipelineLibraryVersion(this) })
            } else {
                null
            }
        }

    }

}
