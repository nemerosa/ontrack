package net.nemerosa.ontrack.extension.jenkins.autoversioning

/**
 * Credentials specification
 *
 * @property type Type of credentials
 * @property id ID of the credentials to inject
 * @property vars List of variable names to bind
 */
data class JenkinsPostProcessingConfigCredentials(
    val type: JenkinsPostProcessingConfigCredentialsType,
    val id: String,
    val vars: List<String>
) {

    /**
     * Renders as a line
     */
    fun renderLine() = "${type.jenkinsName},$id,${vars.joinToString(",")}"

    companion object {

        /**
         * Parsing of lines
         */
        fun parseLines(text: String): List<JenkinsPostProcessingConfigCredentials> {
            val lines = text.lines()
            return lines.map { parseLine(it) }
        }

        private fun parseLine(line: String): JenkinsPostProcessingConfigCredentials {
            // Tokenization
            val tokens = line.split(",").map { it.trim() }
            return if (tokens.size < 3) {
                throw JenkinsPostProcessingConfigCredentialsParseException(
                    line,
                    "Line must contains at least 3 tokens separated by commas."
                )
            } else {
                val typeName = tokens[0]
                val type = JenkinsPostProcessingConfigCredentialsType.values().find {
                    it.jenkinsName == typeName
                }
                        ?: throw JenkinsPostProcessingConfigCredentialsParseException(
                            line,
                            "$typeName is not a supported type of credentials"
                        )
                val id = tokens[1]
                val vars = tokens.drop(2).apply {
                    try {
                        type.check(this)
                    } catch (ex: IllegalArgumentException) {
                        throw JenkinsPostProcessingConfigCredentialsParseException(
                            line,
                            "$typeName variables are not correct: ${ex.message}"
                        )
                    }
                }
                // OK
                JenkinsPostProcessingConfigCredentials(type, id, vars)
            }
        }

    }

}