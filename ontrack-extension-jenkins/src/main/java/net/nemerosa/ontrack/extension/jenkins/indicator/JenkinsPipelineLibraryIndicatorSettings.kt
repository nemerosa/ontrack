package net.nemerosa.ontrack.extension.jenkins.indicator

import net.nemerosa.ontrack.model.support.NameValue

/**
 * Global settings for the [JenkinsPipelineLibraryIndicatorComputer].
 *
 * @see JenkinsPipelineLibraryIndicatorValueTypeConfig
 */
data class JenkinsPipelineLibraryIndicatorSettings(
    val libraryVersions: List<NameValue>,
)