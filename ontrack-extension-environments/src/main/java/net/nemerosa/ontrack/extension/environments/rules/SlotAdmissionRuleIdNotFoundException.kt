package net.nemerosa.ontrack.extension.environments.rules

import net.nemerosa.ontrack.common.BaseException

class SlotAdmissionRuleIdNotFoundException(id: String) : BaseException(
    "Admission rule id '$id' not found."
)
