package net.nemerosa.ontrack.extension.jenkins.indicator

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APILabel

/**
 * Global settings for the [JenkinsPipelineLibraryIndicatorComputer].
 *
 * @see JenkinsPipelineLibraryIndicatorValueTypeConfig
 */
data class JenkinsPipelineLibraryIndicatorSettings(
    @APIDescription("List of libraries and their version requirements")
    @APILabel("Library versions")
    val libraryVersions: List<JenkinsPipelineLibraryIndicatorLibrarySettings>,
) {
    fun findLibrarySettings(name: String) = libraryVersions.find { it.library == name }
}