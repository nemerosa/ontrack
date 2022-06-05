package net.nemerosa.ontrack.extension.jenkins.autoversioning

import net.nemerosa.ontrack.model.exceptions.InputException

/**
 * Parsing errors
 */
class JenkinsPostProcessingConfigCredentialsParseException(line: String, message: String) : InputException("$line parsing error: $message")