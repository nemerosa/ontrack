package net.nemerosa.ontrack.extensions.environments.service

import net.nemerosa.ontrack.common.BaseException

class SlotAdmissionRuleConfigIdNotFound(
    id: String,
) : BaseException(
    "Admission rule config '${id}' not found"
)
