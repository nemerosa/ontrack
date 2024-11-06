package net.nemerosa.ontrack.extension.environments.service

import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.extension.environments.storage.SlotPipelineIdNotFoundException

fun SlotService.getPipelineById(id: String) =
    findPipelineById(id) ?: throw SlotPipelineIdNotFoundException(id)

fun SlotService.findPipelineAdmissionRuleStatusByAdmissionRuleConfigId(
    pipeline: SlotPipeline,
    id: String
) =
    getPipelineAdmissionRuleStatuses(pipeline).find { it.admissionRuleConfig.id == id }
