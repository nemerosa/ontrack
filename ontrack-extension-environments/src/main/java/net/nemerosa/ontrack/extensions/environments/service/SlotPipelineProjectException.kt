package net.nemerosa.ontrack.extensions.environments.service

import net.nemerosa.ontrack.common.BaseException
import net.nemerosa.ontrack.extensions.environments.SlotPipeline

class SlotPipelineProjectException(pipeline: SlotPipeline) : BaseException(
    "The pipeline build project - ${pipeline.build.project.name} - and the slot project - ${pipeline.slot.project.name} - must be the same."
)
