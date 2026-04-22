package net.nemerosa.ontrack.extension.stale

import net.nemerosa.ontrack.common.api.APIDescription

data class AutoDisablingBranchPatternsPropertyCIConfigExtensionData(
    @APIDescription("List of patterns and their behaviour")
    val patterns: List<AutoDisablingBranchPatternsPropertyItem>,
)
