package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.model.annotations.APIDescription

open class ChangeLogTemplatingServiceConfig(
    @APIDescription("String to use to render an empty or non existent change log")
    val empty: String = "",
    @APIDescription("Comma-separated list of project links to follow one by one for a get deep change log. Each item in the list is either a project name, or a project name and qualifier separated by a colon (:).")
    val dependencies: List<String> = emptyList(),
    @APIDescription("Include a title for the change log")
    val title: Boolean = false,
    @APIDescription("Loop over all qualifiers for the last level of `dependencies`, including the default one. Qualifiers at `dependencies` take precedence.")
    val allQualifiers: Boolean = false,
    @APIDescription("If a qualifier has no previous link, uses the default qualifier (empty) qualifier.")
    val defaultQualifierFallback: Boolean = false,
    @APIDescription("Defines how to render commits for a change log")
    val commitsOption: ChangeLogTemplatingCommitsOption = ChangeLogTemplatingCommitsOption.NONE,
) {
    companion object {
        fun emptyValue(configMap: Map<String, String>) =
            configMap[ChangeLogTemplatingServiceConfig::empty.name] ?: ""
    }
}