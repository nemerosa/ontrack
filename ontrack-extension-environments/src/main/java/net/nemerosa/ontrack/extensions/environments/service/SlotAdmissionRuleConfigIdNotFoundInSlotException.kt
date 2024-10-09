package net.nemerosa.ontrack.extensions.environments.service

import net.nemerosa.ontrack.common.BaseException
import net.nemerosa.ontrack.extensions.environments.SlotAdmissionRuleConfig
import net.nemerosa.ontrack.extensions.environments.SlotPipeline

class SlotAdmissionRuleConfigIdNotFoundInSlotException(
    pipeline: SlotPipeline,
    admissionRuleConfig: SlotAdmissionRuleConfig,
) : BaseException(
    "Admission rule config '${admissionRuleConfig.name}' not found in slot '${pipeline.slot}'"
)
