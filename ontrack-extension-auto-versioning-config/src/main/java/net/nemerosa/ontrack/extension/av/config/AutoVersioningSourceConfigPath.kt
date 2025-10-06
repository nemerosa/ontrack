package net.nemerosa.ontrack.extension.av.config

import net.nemerosa.ontrack.graphql.support.IgnoreRef
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIIgnore

@APIDescription("Configuration for an additional path in an auto-versioning configuration.")
data class AutoVersioningSourceConfigPath(
    @APIDescription("Comma-separated list of file to update with the new version")
    val path: String,
    @APIDescription("Regex to use in the target file to identify the line to replace with the new version. The first matching group must be the version.")
    val regex: String? = null,
    @APIDescription("Optional replacement for the regex, using only a property name")
    val property: String? = null,
    @APIDescription("Optional regex to use on the property value")
    val propertyRegex: String? = null,
    @APIDescription("When property is defined, defines the type of property (defaults to Java properties file, but could be NPM, etc.)")
    val propertyType: String? = null,
    @APIDescription("Source of the version for the build. By default, uses the build label is the source project is configured so, or the build name itself. This allows the customization of this behavior.")
    val versionSource: String? = null,
) {
    @APIIgnore
    @IgnoreRef
    val paths: List<String> = toPaths(path)

    companion object {
        fun toPaths(path: String) = path.split(",").map { it.trim() }
        fun toString(paths: List<String>) = paths.joinToString(",")
    }
}