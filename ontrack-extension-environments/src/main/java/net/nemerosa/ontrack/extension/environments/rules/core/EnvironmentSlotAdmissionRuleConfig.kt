package net.nemerosa.ontrack.extension.environments.rules.core

import net.nemerosa.ontrack.extension.environments.Slot

data class EnvironmentSlotAdmissionRuleConfig(
    val environmentName: String,
    val qualifier: String = Slot.DEFAULT_QUALIFIER,
)