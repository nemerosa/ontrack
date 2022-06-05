package net.nemerosa.ontrack.extension.jenkins.autoversioning

/**
 * Rendering as lines
 */
fun List<JenkinsPostProcessingConfigCredentials>.renderLines(): String = joinToString("\n") { it.renderLine() }

/**
 * Rendering as a Jenkins parameter
 */
fun List<JenkinsPostProcessingConfigCredentials>.renderParameter(): String = joinToString("|") { it.renderLine() }
