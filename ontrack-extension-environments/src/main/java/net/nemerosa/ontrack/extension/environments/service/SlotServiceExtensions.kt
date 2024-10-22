package net.nemerosa.ontrack.extension.environments.service

import net.nemerosa.ontrack.extension.environments.SlotPipeline

fun SlotService.findPipelineAdmissionRuleStatusByAdmissionRuleConfigId(
    pipeline: SlotPipeline,
    id: String
) =
    getPipelineAdmissionRuleStatuses(pipeline).find { it.admissionRuleConfig.id == id }
