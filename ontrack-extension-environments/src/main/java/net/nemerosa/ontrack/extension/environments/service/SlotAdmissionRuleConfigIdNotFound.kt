package net.nemerosa.ontrack.extension.environments.service

import net.nemerosa.ontrack.common.BaseException

class SlotAdmissionRuleConfigIdNotFound(
    id: String,
) : BaseException(
    "Admission rule config '${id}' not found"
)
