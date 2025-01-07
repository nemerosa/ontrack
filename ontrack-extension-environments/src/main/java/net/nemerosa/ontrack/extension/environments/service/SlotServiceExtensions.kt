package net.nemerosa.ontrack.extension.environments.service

import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.extension.environments.storage.SlotPipelineIdNotFoundException

fun SlotService.getPipelineById(id: String) =
    findPipelineById(id) ?: throw SlotPipelineIdNotFoundException(id)

fun SlotService.getPipelineAdmissionRuleChecks(pipeline: SlotPipeline) =
    getPipelineAdmissionRuleStatuses(pipeline)
        .map { getAdmissionRuleCheck(it) }

fun SlotService.getPipelineAdmissionRuleChecksForAllRules(pipeline: SlotPipeline) =
    getAdmissionRuleConfigs(pipeline.slot).map { rule ->
        getAdmissionRuleCheck(
            pipeline = pipeline,
            admissionRule = rule,
        )
    }
