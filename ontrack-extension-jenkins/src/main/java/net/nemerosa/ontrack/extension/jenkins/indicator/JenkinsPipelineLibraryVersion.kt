package net.nemerosa.ontrack.extension.jenkins.indicator

import net.nemerosa.ontrack.common.Version

/**
 * Version for a Jenkins pipeline library.
 *
 * @property value Text value for the version
 */
data class JenkinsPipelineLibraryVersion(
    val value: String
) : Comparable<JenkinsPipelineLibraryVersion> {
    override fun compareTo(other: JenkinsPipelineLibraryVersion): Int {
        val thisVersion = Version.parseVersion(this.value)
        val otherVersion = Version.parseVersion(other.value)
        return if (thisVersion != null) {
            if (otherVersion != null) {
                thisVersion.compareTo(otherVersion)
            } else {
                1 // Semantic version always greater than a non semantic one
            }
        } else if (otherVersion != null) {
            -1 // Semantic version always greater than a non semantic one
        } else {
            // Comparing on text when no semantic version
            this.value.compareTo(other.value)
        }
    }
}
