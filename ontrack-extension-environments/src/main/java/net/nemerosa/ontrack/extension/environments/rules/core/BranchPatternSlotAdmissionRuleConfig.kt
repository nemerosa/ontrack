package net.nemerosa.ontrack.extension.environments.rules.core

data class BranchPatternSlotAdmissionRuleConfig(
    val includes: List<String> = emptyList(),
    val excludes: List<String> = emptyList(),
)
