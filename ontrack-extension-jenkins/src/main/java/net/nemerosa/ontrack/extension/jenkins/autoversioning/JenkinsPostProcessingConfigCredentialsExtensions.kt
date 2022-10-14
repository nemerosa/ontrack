package net.nemerosa.ontrack.extension.jenkins.autoversioning

/**
 * Rendering as a Jenkins parameter
 */
fun List<JenkinsPostProcessingConfigCredentials>.renderParameter(): String = joinToString("|") { it.renderLine() }
