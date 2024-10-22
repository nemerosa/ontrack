package net.nemerosa.ontrack.extension.environments.ui

import net.nemerosa.ontrack.extension.environments.SlotPipelineStatus
import net.nemerosa.ontrack.graphql.schema.AbstractGQLEnum
import org.springframework.stereotype.Component

@Component
class GQLEnumSlotPipelineStatus : AbstractGQLEnum<SlotPipelineStatus>(
    type = SlotPipelineStatus::class,
    values = SlotPipelineStatus.values(),
    description = "List of pipelines for a status",
)