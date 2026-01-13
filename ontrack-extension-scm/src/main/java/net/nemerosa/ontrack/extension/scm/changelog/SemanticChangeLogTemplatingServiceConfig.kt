package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.graphql.support.ListRef
import net.nemerosa.ontrack.model.annotations.APIDescription

open class SemanticChangeLogTemplatingServiceConfig(
    @APIDescription("Must a section for changelog actual issues be present?")
    val issues: Boolean = false,
    dependencies: List<String> = emptyList(),
    @APIDescription("Mapping types to section titles")
    override val sections: List<SemanticChangeLogSection> = emptyList(),
    @APIDescription("Types to exclude")
    override val exclude: List<String> = emptyList(),
    @APIDescription("Loop over all qualifiers for the last level of `dependencies`, including the default one. Qualifiers at `dependencies` take precedence.")
    val allQualifiers: Boolean = false,
    @APIDescription("If a qualifier has no previous link, uses the default qualifier (empty) qualifier.")
    val defaultQualifierFallback: Boolean = false,
) : SemanticChangeLogConfig {
    @APIDescription("Comma-separated list of project links to follow one by one for a get deep change log. Each item in the list is either a project name, or a project name and qualifier separated by a colon (:).")
    @ListRef
    val dependencies: List<String> = dependencies.flatMap {
        it.split(",").map { item -> item.trim() }
    }
}
