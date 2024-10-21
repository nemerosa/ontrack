package net.nemerosa.ontrack.extensions.environments.rules.core

data class BranchPatternSlotAdmissionRuleConfig(
    val includes: List<String> = emptyList(),
    val excludes: List<String> = emptyList(),
)
