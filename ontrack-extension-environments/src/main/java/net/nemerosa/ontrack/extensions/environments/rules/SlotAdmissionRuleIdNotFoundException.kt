package net.nemerosa.ontrack.extensions.environments.rules

import net.nemerosa.ontrack.common.BaseException

class SlotAdmissionRuleIdNotFoundException(id: String) : BaseException(
    "Admission rule id '$id' not found."
)
