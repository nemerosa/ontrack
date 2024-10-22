package net.nemerosa.ontrack.extension.environments.service

import net.nemerosa.ontrack.common.BaseException
import net.nemerosa.ontrack.extension.environments.SlotPipeline

class SlotPipelineProjectException(pipeline: SlotPipeline) : BaseException(
    "The pipeline build project - ${pipeline.build.project.name} - and the slot project - ${pipeline.slot.project.name} - must be the same."
)
