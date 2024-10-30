package net.nemerosa.ontrack.extension.environments.service

import net.nemerosa.ontrack.extension.environments.SlotAdmissionRuleConfig
import net.nemerosa.ontrack.model.exceptions.InputException

class SlotAdmissionRuleConfigNameFormatException(
    name: String,
) : InputException(
    "Admission rule names must match ${SlotAdmissionRuleConfig.PATTERN} but was: $name"
)
