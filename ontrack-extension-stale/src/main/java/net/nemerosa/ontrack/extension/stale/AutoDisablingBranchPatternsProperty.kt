package net.nemerosa.ontrack.extension.stale

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.DocumentationField

/**
 * Configuration of branch patterns to be automatically disabled.
 */
data class AutoDisablingBranchPatternsProperty(
    @APIDescription("List of patterns and their behaviour")
    @DocumentationField
    val items: List<AutoDisablingBranchPatternsPropertyItem>,
)
