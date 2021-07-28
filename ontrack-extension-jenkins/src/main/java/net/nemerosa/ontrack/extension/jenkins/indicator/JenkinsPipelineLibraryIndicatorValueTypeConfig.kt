package net.nemerosa.ontrack.extension.jenkins.indicator

data class JenkinsPipelineLibraryIndicatorValueTypeConfig(
    val versionRequired: Boolean,
    val versionMinimum: JenkinsPipelineLibraryVersion?
)
