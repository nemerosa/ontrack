package net.nemerosa.ontrack.extension.environments.service

import net.nemerosa.ontrack.common.BaseException
import net.nemerosa.ontrack.extension.environments.SlotAdmissionRuleConfig
import net.nemerosa.ontrack.extension.environments.SlotPipeline

class SlotAdmissionRuleConfigIdNotFoundInSlotException(
    pipeline: SlotPipeline,
    admissionRuleConfig: SlotAdmissionRuleConfig,
) : BaseException(
    "Admission rule config '${admissionRuleConfig.name}' not found in slot '${pipeline.slot}'"
)
