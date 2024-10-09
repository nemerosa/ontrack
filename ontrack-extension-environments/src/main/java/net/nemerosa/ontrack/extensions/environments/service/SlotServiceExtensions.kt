package net.nemerosa.ontrack.extensions.environments.service

import net.nemerosa.ontrack.extensions.environments.SlotPipeline

fun SlotService.findPipelineAdmissionRuleStatusByAdmissionRuleConfigId(
    pipeline: SlotPipeline,
    id: String
) =
    getPipelineAdmissionRuleStatuses(pipeline).find { it.admissionRuleConfig.id == id }
